package org.firstinspires.ftc.teamcode.robot.subsystem;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Outtake {
    private Servo outtakeBar;
    private Servo outtakeClaw;

    // TODO: Change to static final double later
    private double OUTTAKE_TRANSFER = 0.5;
    private double OUTTAKE_SCORE = 0.6;

    private double CLAW_CLOSE = 0.5;
    private double CLAW_OPEN = 0.6;


    public Outtake(HardwareMap hardwareMap) {
        outtakeBar = hardwareMap.get(Servo.class, "outtakeBar");
        outtakeClaw = hardwareMap.get(Servo.class, "outtakeClaw");

        // TODO: Reverse actuators if needed
    }

    public void runDebug(GamepadEx gamepadEx) {
        // outtake bar config
        if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
            OUTTAKE_TRANSFER -= 0.01;
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
            OUTTAKE_TRANSFER += 0.01;
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.X)) {
            OUTTAKE_SCORE -= 0.01;
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.Y)) {
            OUTTAKE_SCORE += 0.01;
        }

        // outtake claw config
    }
}
