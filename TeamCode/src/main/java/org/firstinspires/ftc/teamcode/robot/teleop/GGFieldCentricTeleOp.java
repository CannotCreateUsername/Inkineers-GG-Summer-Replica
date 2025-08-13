package org.firstinspires.ftc.teamcode.robot.teleop;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;
import org.firstinspires.ftc.teamcode.robot.subsystem.Intake;
import org.firstinspires.ftc.teamcode.robot.subsystem.Outtake;

/**
 * This is an example teleop that showcases movement and field-centric driving.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 12/30/2024
 */

@TeleOp(name = "Golden Gears", group = "Teleop")
public class GGFieldCentricTeleOp extends OpMode {
    private Follower follower;
    private final Pose startPose = new Pose(0,0,0);

    private Intake intake;
    private Outtake outtake;

    GamepadEx gamepadEx;

    /** This method is call once when init is played, it initializes the follower **/
    @Override
    public void init() {
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(startPose);

        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);

        gamepadEx = new GamepadEx(gamepad1);
    }

    /** This method is called continuously after Init while waiting to be started. **/
    @Override
    public void init_loop() {
    }

    /** This method is called once at the start of the OpMode. **/
    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    /** This is the main loop of the opmode and runs continuously after play **/
    @Override
    public void loop() {

        /* Update Pedro to move the robot based on:
        - Forward/Backward Movement: -gamepad1.left_stick_y
        - Left/Right Movement: -gamepad1.left_stick_x
        - Turn Left/Right Movement: -gamepad1.right_stick_x
        - Robot-Centric Mode: false
        */

        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, false);
        follower.update();

        // Outtake Logic. A = Next State, X = In, B = Ready, Y = Out
        if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
            outtake.nextState();
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.X)) {
            outtake.setState(Outtake.MasterState.OUTTAKE_IN);
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
            outtake.setState(Outtake.MasterState.OUTTAKE_READY);
        } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.Y)) {
            outtake.setState(Outtake.MasterState.OUTTAKE_OUT);
        }
        outtake.runOuttakeSubsystem();

        // Intake Logic.
        intake.runIntakeSubsystem(gamepadEx, gamepad1);

        /* Telemetry Outputs of our Follower */
        telemetry.addLine("\\PEDRO TELEMETRY//");
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));

        /* Intake Telemetry */
        telemetry.addLine("\\INTAKE TELEMETRY//");
        telemetry.addData("Intake State", intake.getMasterState());
        telemetry.addData("Intake Slide Target", intake.getTargetSlidePosition());
        telemetry.addData("Intake Slide Position", intake.getIntakeSlidePosition());

        /* Outtake Telemetry */
        telemetry.addLine("\\OUTTAKE TELEMETRY//");
        telemetry.addData("Outtake State", outtake.getMasterState());
        telemetry.addData("Outtake Slide Target", outtake.getTargetSlidePosition());
        telemetry.addData("Outtake Slide Position", outtake.getOuttakeSlidePosition());

        /* Update Telemetry to the Driver Hub */
        telemetry.update();

    }

    /** We do not use this because everything automatically should disable **/
    @Override
    public void stop() {
    }
}