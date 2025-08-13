package org.firstinspires.ftc.teamcode.robot.subsystem;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    private DcMotor intakeSlides;
    private Servo intakeBar;
    private CRServo intake;

    public Intake(HardwareMap hardwareMap) {
        intakeSlides = hardwareMap.get(DcMotor.class, "intakeSlides");
        intakeBar = hardwareMap.get(Servo.class, "intakeBar");
        intake = hardwareMap.get(CRServo.class, "intake");

        // TODO: Reverse actuators if needed
    }


}
