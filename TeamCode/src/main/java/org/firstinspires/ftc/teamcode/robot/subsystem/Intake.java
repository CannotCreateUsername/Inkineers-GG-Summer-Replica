package org.firstinspires.ftc.teamcode.robot.subsystem;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {
    private final DcMotor intakeSlides;
    private final Servo intakeBar;
    private final CRServo intake;

    public enum MasterState {
        INTAKE_IN,
        INTAKE_OUT
    }

    public enum BarState {
        TRANSFER,
        PICKUP
    }

    private MasterState masterState;
    private BarState barState;

    /* PID Variables */
    private int targetSlidePosition = 0;

    private static final double kP = 0.04;
    private static final double kI = 0.00;
    private static final double kD = 0.00;

    private double previous_time;
    private double previous_time_bumper = 0;
    private int right_bumper_press_count = 0;
    private double previous_error;

    private final ElapsedTime runtime;
    /* PID */

    // TODO: Fine tune these values.
    private static final int INTAKE_SLIDE_OUT_POS = 200;
    private static final int INTAKE_SLIDE_IN_POS = 0;
    private static final int SLIDE_INCREMENT = 10;

    private static final double INTAKE_BAR_OUT_POS = 0.5;
    private static final double INTAKE_BAR_IN_POS = 0.5;

    private static final double INTAKE_POW = 1;

    private final ElapsedTime switchTime;

    public Intake(HardwareMap hardwareMap) {
        intakeSlides = hardwareMap.get(DcMotor.class, "intakeSlides");
        intakeBar = hardwareMap.get(Servo.class, "intakeBar");
        intake = hardwareMap.get(CRServo.class, "intake");

        intakeSlides.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeSlides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: Reverse actuators if needed
        intakeSlides.setDirection(DcMotorSimple.Direction.REVERSE);

        masterState = MasterState.INTAKE_IN;
        barState = BarState.TRANSFER;

        runtime = new ElapsedTime();
        switchTime = new ElapsedTime();
    }

    // FOR TELEOP
    public void runIntakeSubsystem(GamepadEx gamepadEx, Gamepad gamepad) {
        switch (masterState) {
            case INTAKE_IN:
                if (gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                    setTargetSlidePos(INTAKE_SLIDE_OUT_POS);
                    setState(MasterState.INTAKE_OUT);
                }
                break;
            case INTAKE_OUT:
                if (switchTime.seconds() > 0.4)
                    barState = BarState.PICKUP;

                if (gamepad.right_trigger > 0) {
                    spinIntakeIn();
                } else if (gamepad.left_trigger > 0) {
                    spinIntakeOut();
                } else {
                    stopIntake();
                }

                // Extend slides if right bumper is held
                if (gamepad.right_bumper) {
                    setTargetSlidePos(targetSlidePosition + SLIDE_INCREMENT);
                }
                // Retract slides if left bumper is held
                if (gamepad.left_bumper) {
                    setTargetSlidePos(targetSlidePosition - SLIDE_INCREMENT);
                }

                if (gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                    right_bumper_press_count++;
                    if (runtime.milliseconds() - previous_time_bumper < 500 && right_bumper_press_count >= 2) {
                        setTargetSlidePos(INTAKE_SLIDE_IN_POS);
                        barState = BarState.TRANSFER;
                        setState(MasterState.INTAKE_IN);
                        right_bumper_press_count = 0; // Reset count after double press
                    }
                    previous_time_bumper = runtime.milliseconds();
                }
                break;
        }

        // Extension
        switch (barState) {
            case TRANSFER:
                retractIntakeBar();
                break;
            case PICKUP:
                extendIntakeBar();
                break;
        }

        // Linear Slides
        runIntakeSlides();
    }

    public void setState(MasterState state) {
        masterState = state;
        switchTime.reset();
    }

    // PRIMITIVE METHODS
    public void spinIntakeIn() {
        intake.setPower(INTAKE_POW);
    }

    public void spinIntakeOut() {
        intake.setPower(-INTAKE_POW * 0.75);
    }

    public void stopIntake() {
        intake.setPower(0);
    }

    public void extendIntakeBar() {
        intakeBar.setPosition(INTAKE_BAR_OUT_POS);
    }

    public void retractIntakeBar() {
        intakeBar.setPosition(INTAKE_BAR_IN_POS);
    }

    private void setTargetSlidePos(int ticks) {
        // Don't extend or retract too much
        targetSlidePosition = Math.max(0, Math.min(INTAKE_SLIDE_OUT_POS, ticks));
    }

    private void runIntakeSlides() {
        double current_time = runtime.milliseconds();
        double current_error = targetSlidePosition - intakeSlides.getCurrentPosition();

        double p = kP * current_error;
        double i = kI * (current_error * (current_time - previous_time));
        double d = kD * (current_error - previous_error) / (current_time - previous_time);

        previous_time = current_time;
        previous_error = current_error;

        // Control intake slides motor
        intakeSlides.setPower(p + i + d);
    }

    // ACCESSOR METHODS
    public int getTargetSlidePosition() {
        return targetSlidePosition;
    }

    public int getIntakeSlidePosition() {
        return intakeSlides.getCurrentPosition();
    }

    public String getMasterState() {
        return masterState.name();
    }
}
