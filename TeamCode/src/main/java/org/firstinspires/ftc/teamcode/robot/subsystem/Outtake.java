package org.firstinspires.ftc.teamcode.robot.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Outtake {
    private final DcMotor outtakeSlidesLeft, outtakeSlidesRight;
    private final Servo outtakeBar;
    private final Servo outtakeClaw;

    public enum MasterState {
        OUTTAKE_IN,
        OUTTAKE_READY,
        OUTTAKE_OUT,
    }

    public enum BarState {
        TRANSFER,
        SCORE
    }

    public enum ClawState {
        OPEN,
        CLOSE
    }

    private MasterState masterState;
    private BarState barState;
    private ClawState clawState;

    /* PID Variables */
    private int targetSlidePosition = 0;

    private static final double kP = 0.04;
    private static final double kI = 0.00;
    private static final double kD = 0.00;

    private double previous_time;
    private double previous_error;

    private final ElapsedTime runtime;
    /* PID */

    private static final int OUTTAKE_SLIDE_OUT_POS = 1600;
    private static final int OUTTAKE_SLIDE_IN_POS = 0;

    private static final double OUTTAKE_TRANSFER_POS = 0.5;
    private static final double OUTTAKE_SCORE_POS = 0.6;

    private static final double CLAW_CLOSE_POS = 0.5;
    private static final double CLAW_OPEN_POS = 0.6;

    private final ElapsedTime switchTime;

    public Outtake(HardwareMap hardwareMap) {
        outtakeBar = hardwareMap.get(Servo.class, "outtakeBar");
        outtakeClaw = hardwareMap.get(Servo.class, "outtakeClaw");

        outtakeSlidesLeft = hardwareMap.get(DcMotor.class, "outtakeSlidesLeft");
        outtakeSlidesRight = hardwareMap.get(DcMotor.class, "outtakeSlidesRight");

        outtakeSlidesLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeSlidesRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeSlidesLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeSlidesRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeSlidesLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtakeSlidesRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: Reverse actuators if needed

        masterState = MasterState.OUTTAKE_IN;
        barState = BarState.TRANSFER;
        clawState = ClawState.OPEN;

        runtime = new ElapsedTime();
        switchTime = new ElapsedTime();
    }

    // FOR TELEOP
    public void runOuttakeSubsystem() {
        switch (masterState) {
            case OUTTAKE_IN:
                clawState = ClawState.OPEN;
                if (switchTime.seconds() > 0.4) {
                    barState = BarState.TRANSFER;
                    setTargetSlidePos(OUTTAKE_SLIDE_IN_POS);
                }
                break;
            case OUTTAKE_READY:
                clawState = ClawState.CLOSE;
                break;
            case OUTTAKE_OUT:
                barState = BarState.SCORE;
                setTargetSlidePos(OUTTAKE_SLIDE_OUT_POS);
                break;
        }

        // Bar
        switch (barState) {
            case TRANSFER:
                retractBar();
                break;
            case SCORE:
                extendBar();
                break;
        }

        // Claw
        switch (clawState) {
            case OPEN:
                openClaw();
                break;
            case CLOSE:
                closeClaw();
                break;
        }

        // Slides
        runOuttakeSlides();
    }

    public void setState(MasterState state) {
        masterState = state;
        switchTime.reset();
    }

    public void nextState() {
        switch (masterState) {
            case OUTTAKE_IN:
                setState(MasterState.OUTTAKE_READY);
                break;
            case OUTTAKE_READY:
                setState(MasterState.OUTTAKE_OUT);
                break;
            case OUTTAKE_OUT:
                setState(MasterState.OUTTAKE_IN);
                break;
        }
    }

    // NOT FOR TELEOP
    private void setTargetSlidePos(int ticks) {
        targetSlidePosition = ticks;
    }

    // Using LEFT motor encoder
    private void runOuttakeSlides() {
        double current_time = runtime.milliseconds();
        double current_error = targetSlidePosition - outtakeSlidesLeft.getCurrentPosition();

        double p = kP * current_error;
        double i = kI * (current_error * (current_time - previous_time));
        double d = kD * (current_error - previous_error) / (current_time - previous_time);

        previous_time = current_time;
        previous_error = current_error;

        // Control outtake slides motor
        double power = p + i + d;
        outtakeSlidesLeft.setPower(power);
        outtakeSlidesRight.setPower(-power);
    }

    // ACCESSOR METHODS
    public int getTargetSlidePosition() {
        return targetSlidePosition;
    }

    public int getOuttakeSlidePosition() {
        return outtakeSlidesLeft.getCurrentPosition();
    }

    public String getMasterState() {
        return masterState.name();
    }

    // PRIMITIVE METHODS
    public void extendBar() {
        outtakeBar.setPosition(OUTTAKE_TRANSFER_POS);
    }

    public void retractBar() {
        outtakeBar.setPosition(OUTTAKE_SCORE_POS);
    }

    public void openClaw() {
        outtakeClaw.setPosition(CLAW_OPEN_POS);
    }

    public void closeClaw() {
        outtakeClaw.setPosition(CLAW_CLOSE_POS);
    }

}
