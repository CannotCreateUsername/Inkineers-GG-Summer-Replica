package org.firstinspires.ftc.teamcode.robot.teleop;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class GGIntakePositionTesting extends LinearOpMode {

    double intakeBarPos = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor intakeSlides = hardwareMap.get(DcMotor.class, "intakeSlides");
        Servo intakeBar = hardwareMap.get(Servo.class, "intakeBar");
        // CRServo intake = hardwareMap.get(CRServo.class, "intake");

        intakeSlides.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeSlides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        GamepadEx gamepadEx = new GamepadEx(gamepad1);

        waitForStart();
        while (opModeIsActive()) {
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
                intakeBarPos += 0.01;
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
                intakeBarPos -= 0.01;
            }

            intakeBar.setPosition(intakeBarPos);

            telemetry.addData("Intake Bar Position", intakeBarPos);
            telemetry.addData("Slide Position", intakeSlides.getCurrentPosition());
            telemetry.update();
        }
    }
}
