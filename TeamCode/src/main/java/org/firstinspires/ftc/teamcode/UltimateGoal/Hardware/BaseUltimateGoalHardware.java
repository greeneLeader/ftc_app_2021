package org.firstinspires.ftc.teamcode.UltimateGoal.Hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Shared.Hardware.BaseCamera;
import org.firstinspires.ftc.teamcode.Shared.Hardware.MockDcMotor;

public class BaseUltimateGoalHardware {
    public static final String WEB_CAM_NAME = "Webcam 1";
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    public static final String ELEMENT_QUAD = "Quad";
    public static final String ELEMENT_SINGLE = "Single";
    // Instance Variables
    public DcMotor leftMotor = null;
    public DcMotor rightMotor = null;
    BNO055IMU imu;
    public BaseCamera webCamera = new BaseCamera();
    public static double robotTurnRampDownAngle = 45;
    public static double robotTurnStopAngle = 5;

    public void init(HardwareMap hwMap) {
        // Define and Initialize Motors
        leftMotor = getMotor(hwMap, "left_drive");
        rightMotor = getMotor(hwMap, "right_drive");

        // Setting left motor to reverse, making the robot moveable now.
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public String initWebCamera(HardwareMap hardwareMap){
        return webCamera.initWebCamera(hardwareMap, WEB_CAM_NAME);
    }

    public String initPhoneCamera(HardwareMap hardwareMap){
        return webCamera.initBackCamera(hardwareMap);
    }
    
    public String initTfod(){
        return webCamera.initTfod(.8, TFOD_MODEL_ASSET, ELEMENT_QUAD, ELEMENT_SINGLE);
    }

    public float getAngle() {
        // Gets orientation of robot.
        Orientation angularOrientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        // Returns the angle of the robot.
        return angularOrientation.firstAngle;
    }

    /**
     * This method determines the speed of a motor.
     * @param desiredAngle our desired angle
     * @param robotAngle the robot's current angle
     * @return
     */
    public double getMotorTurnSpeed(double desiredAngle, double robotAngle){
        // Calculate the angle difference between our desired angle and the actual angle of
        // the robot.
        double angleDifference = AngleUnit.normalizeDegrees(desiredAngle - robotAngle);

        // Declare the speed variable for later use.
        double speed;

        // If the angle difference is greater than 10 the robot will turn counterclockwise.
        // Otherwise, if the angle difference is less than -10 the robot will turn clockwise.
        if (Math.abs(angleDifference) > robotTurnStopAngle) {
            // Equation for determining target speed: Speed = angle / robotTurnRampDownAngle.
            speed = angleDifference/robotTurnRampDownAngle;
        }
        // Otherwise, we don't want the robot to turn at all.
        else {
            speed = 0;
        }

        // Return the speed that the motor should be turning to.
        return speed;
    }

    public DcMotor getMotor(HardwareMap hwMap, String name) {
        try {
            return hwMap.dcMotor.get(name);
        } catch (Exception e){
            return new MockDcMotor();
        }
    }
}