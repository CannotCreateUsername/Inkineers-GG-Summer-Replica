package org.firstinspires.ftc.teamcode.robot.teleop;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Outtake Position Testing", group = "Testing")
public class GGOuttakePositionTesting extends LinearOpMode {

    double outtakeBarPos = 0.5;
    double outtakeClawPos = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        Servo outtakeBar = hardwareMap.get(Servo.class, "outtakeBar");
        Servo outtakeClaw = hardwareMap.get(Servo.class, "outtakeClaw");
        DcMotor outtakeSlidesLeft = hardwareMap.get(DcMotor.class, "outtakeSlidesLeft");

        outtakeSlidesLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeSlidesLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeSlidesLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        GamepadEx gamepadEx = new GamepadEx(gamepad1);

        waitForStart();
        while (opModeIsActive()) {
            // outtake bar config
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
                outtakeBarPos += 0.01;
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
                outtakeBarPos -= 0.01;
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.X)) {
                outtakeClawPos -= 0.01;
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.Y)) {
                outtakeClawPos += 0.01;
            }

            outtakeBar.setPosition(outtakeBarPos);
            outtakeClaw.setPosition(outtakeClawPos);

            telemetry.addData("Outtake Bar Position", outtakeBarPos);
            telemetry.addData("Outtake Claw Position", outtakeClawPos);
            telemetry.addData("Slide Position", outtakeSlidesLeft.getCurrentPosition());
            telemetry.update();
        }
    }
}
