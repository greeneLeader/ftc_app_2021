package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ProgrammingUltimateGoalHardware {
    public DcMotor leftMotor = null;
    public DcMotor rightMotor = null;
    public DcMotor middleMotor = null;

    public void init(HardwareMap hwMap){
        leftMotor = hwMap.dcMotor.get("left_drive");
        rightMotor = hwMap.dcMotor.get("right_drive");
        middleMotor = hwMap.dcMotor.get("middle_drive");

        leftMotor.setDirection(DcMotor.Direction.REVERSE);

    }
}