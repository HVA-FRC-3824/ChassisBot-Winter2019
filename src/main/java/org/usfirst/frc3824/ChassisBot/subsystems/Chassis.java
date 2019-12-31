// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc3824.ChassisBot.subsystems;

import org.usfirst.frc3824.ChassisBot.Robot;
import org.usfirst.frc3824.ChassisBot.commands.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;

import com.ctre.phoenix.motorcontrol.ControlMode;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class Chassis extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private WPI_TalonSRX leftMaster;
    private WPI_VictorSPX leftSlave;
    private SpeedControllerGroup left;
    private WPI_TalonSRX rightMaster;
    private WPI_VictorSPX rightSlave;
    private SpeedControllerGroup right;
    private DifferentialDrive differentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    /********************************
     * Declaring navX sensor object *
     ********************************/
    private static AHRS ahrs;

    /************************************
     * Declaring path following objects *
     ************************************/
    private EncoderFollower left_follower;
    private EncoderFollower right_follower;

    private double max_velocity = 2.63;
    private double wheel_diameter = 0.1524;
    private int ticksPerRev = 4096;

    private boolean pathIsReversed = false;

    private int angleCorrectionCounter = 16;

    private boolean pathFollowingComplete = false;

    public Chassis() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        leftMaster = new WPI_TalonSRX(0);
        
        
        
        leftSlave = new WPI_VictorSPX(1);
        
        
        
        left = new SpeedControllerGroup(leftMaster, leftSlave  );
        addChild("Left",left);
        
        
        rightMaster = new WPI_TalonSRX(2);
        
        
        
        rightSlave = new WPI_VictorSPX(3);
        
        
        
        right = new SpeedControllerGroup(rightMaster, rightSlave  );
        addChild("Right",right);
        
        
        differentialDrive = new DifferentialDrive(left, right);
        addChild("Differential Drive",differentialDrive);
        differentialDrive.setSafetyEnabled(true);
        differentialDrive.setExpiration(0.1);
        differentialDrive.setMaxOutput(1.0);

        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

        /**************************
         * Configure Drive Motors *
         **************************/
        leftMaster.setInverted(false);
        leftMaster.setSensorPhase(true);

        // not ".follow"ing b/c both are in the differentialDrive object
        leftSlave.setInverted(false);
        // leftSlave.follow(leftMaster);

        rightMaster.setInverted(true);
        rightMaster.setSensorPhase(true);

        // not ".follow"ing b/c both are in the differentialDrive object
        rightSlave.setInverted(true);
        // rightSlave.follow(rightMaster);

        /**********************************************************************
         * try to instantiate navX AHRS object -- communicate via MXP SPI Bus * 
         **********************************************************************/
        try {
            ahrs = new AHRS(SPI.Port.kMXP);
        } catch (RuntimeException ex) {
            System.out.println("\nError instantiating navX-MXP:\n" + ex.getMessage() + "\n");
        }

        // Reset motor encoder positions
        this.resetPositions();

        // Reset gyro angle value for accurate spline heading following
        this.resetGyro();
    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new TeleopDrive());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    /**********************************************
     * Control teleop driving with joystick input *
     **********************************************/
    public void driveWithJoystick(double power, double turn) {
        differentialDrive.arcadeDrive(turn, -power);
    }

    /************************************************
     * Get encoder and gyro values for spline autos *
     ************************************************/
    public int getLeftPosition() {
        return leftMaster.getSelectedSensorPosition(0);
    }
    public int getRightPosition() {
        return rightMaster.getSelectedSensorPosition(0);
    }

    /***************************************
     * Reset drive motor encoder positions *
     ***************************************/
    public void resetPositions() {
        leftMaster.setSelectedSensorPosition(0);
        rightMaster.setSelectedSensorPosition(0);
    }

    /**********************
     * Gyro Angle Methods *
     **********************/
    public double getGyroAngle() { // get angle of gyro
        return ahrs.getAngle();
    }
    public void resetGyro() { // reset gyro
        ahrs.reset();
    }

    /************************************
     * Set motor power for spline autos *
     ************************************/
    public void setMotorOutputs(double leftPower, double rightPower) {
        leftMaster.set(ControlMode.PercentOutput, leftPower);
        leftSlave.set(ControlMode.PercentOutput, leftPower);
        rightMaster.set(ControlMode.PercentOutput, rightPower);
        rightSlave.set(ControlMode.PercentOutput, rightPower);
    }
    
    /************************************************
     * Initialize objects for spline path following *
     ************************************************/
    public void initializePathFollowing(Trajectory passed_left_traj, Trajectory passed_right_traj, boolean isReversed) {
        
        // Tell command group that the robot is still following a path --> don't move onto next command
        pathFollowingComplete = false;

        // Set "pathIsReversed" to passed in "isReversed" to tell other methods within Chassis class that path is reversed
        pathIsReversed = isReversed;

        // If path is reversed, change encoder values to be negative when going forward and positive when
        // going backwards to follow path encoder waypoints.
        if (pathIsReversed) {
            leftMaster.setSensorPhase(false);
            rightMaster.setSensorPhase(false);
        }

        // Initialize encoder followers using trajectories
        left_follower = new EncoderFollower(passed_left_traj);
        right_follower = new EncoderFollower(passed_right_traj);

        // Configure encoder followers based on current ticks, ticks per rev, and wheel diameter
        left_follower.configureEncoder(this.getLeftPosition(), ticksPerRev, wheel_diameter);
        right_follower.configureEncoder(this.getRightPosition(), ticksPerRev, wheel_diameter);

        // Configure PIDVA values for encoder followers (Proportional, Integral, Derivative, 1 divided by
        // max velocity, acceleration)
        left_follower.configurePIDVA(1.0, 0.0, 0.0, 1 / max_velocity, 0);
        right_follower.configurePIDVA(1.0, 0.0, 0.0, 1 / max_velocity, 0);
    }

    /*******************************************************************
     * Get angle difference using left and right difference comparison *
     *******************************************************************/
    public double getAngleDifferenceWithLeftRight(double positiveAngle, double negativeAngle, boolean gyroIsPositive) {
        // Creating variables that will hold the difference between angles on the left and right sides of the spectrum.
        double leftDifference = 0;
        double rightDifference = 0;

        // These algorithms calculate the difference on the left and right side using
        // the positive and negative angle passed in.
        leftDifference = Math.abs(negativeAngle) + positiveAngle;
        rightDifference = (180 - Math.abs(negativeAngle)) + (180 - positiveAngle);

        // Return the fastest route (smaller difference) to reaching the desired angle from the gyro angle.
        // Depending on if gyro angle is positive/negative, difference may be positive/negative 
        // for motor ouput to be positive/negative.
        if (leftDifference < rightDifference) {
            if (gyroIsPositive)
                leftDifference *= -1.0;
            return leftDifference;
        }
        else if (leftDifference > rightDifference) {
            if (!gyroIsPositive)
                rightDifference *= -1.0;
            return rightDifference;
        }
        else if (leftDifference == rightDifference)
            return 180;
        else {
            System.out.println("\nERROR: Could not calculate angle difference. (left and right difference would not compute)\n");
            return 0;
        }
    }

    /*******************************************************************************
     * Calculate the angle difference between the gyro heading and desired heading *
     *******************************************************************************/
    public double getAngleDifference() {
        // Get gyro heading and desired heading from 0-360 degrees (x modulus 360 accomplishes this)
        double gyro_heading = this.getGyroAngle() % 360;
        double desired_heading = Pathfinder.r2d(left_follower.getHeading()) % 360;
        // Create variable that will hold the difference in angle
        double angleDifference = 0;

        // Change gyro heading and desired heading to a degree between -180 and 180
        // Example: 270 degrees is translated to -90 degrees
        // This allows angleDifference to be efficiently computed
        if (gyro_heading > 180) {
            gyro_heading = gyro_heading - 360;
        } else if (gyro_heading < -180) {
            gyro_heading = gyro_heading + 360;
        }
        if (desired_heading > 180) {
            desired_heading = desired_heading - 360;
        } else if (desired_heading < -180) {
            desired_heading = desired_heading + 360;
        }

        // If path is reversed, robot will be facing in the opposite direction and 
        // thus the gyro heading will be 180 degrees off --> this compensates for that
        if (pathIsReversed)
            gyro_heading -= 180;

        // Calculate the angle difference keeping in mind the fastest route between angles
        // Example: Gyro Angle: -179 degrees, Desired Angle: 179 degrees,
        // set angle difference as -2 degrees, not 358 degrees.
        if ((gyro_heading > 0 && desired_heading > 0) || (gyro_heading < 0 && desired_heading < 0))
            angleDifference = desired_heading - gyro_heading;
        else if (gyro_heading > 0 && desired_heading < 0)
            angleDifference = getAngleDifferenceWithLeftRight(gyro_heading, desired_heading, true);
        else if (gyro_heading < 0 && desired_heading > 0)
            angleDifference = getAngleDifferenceWithLeftRight(desired_heading, gyro_heading, false);
        else if ((gyro_heading == 0 && desired_heading > 0) || (gyro_heading == 0 && desired_heading < 0))
            angleDifference = desired_heading;
        else if ((gyro_heading > 0 && desired_heading == 0) || (gyro_heading < 0 && desired_heading == 0))
            angleDifference = -gyro_heading;
        else if (gyro_heading == desired_heading)
            angleDifference = 0;
        else
            System.out.println("\nERROR: Could not calculate angle difference. (no criteria met)\n");

        // Send angleDifference value back to caller
        return angleDifference;
    }

    /**********************************************************
     * Follow spline paths using encoder followers and a gyro *
     **********************************************************/
    public void followPath() {
        // Declare variables that will help with turning based on gyro and desired headings
        double angleDifference;
        double turn;

        if (left_follower.isFinished() || right_follower.isFinished()) {
            // For initial "angleCorrectionCounter" multiplied by time between each waypoint,
            // correct the ending gyro angle to desired angle.
            // NOTE: For a more accurate final angle correction, use an angle PID or vision tracking.
            if (angleCorrectionCounter > 0) {
                // Get difference in angle in degrees
                angleDifference = this.getAngleDifference();
                // Use algorithm below to translate angleDifference from degrees to motor output (-1.0 to 1.0)
                if (!pathIsReversed)
                    turn = (1.0/20.0) * angleDifference;
                else
                    turn = (1.0/40.0) * angleDifference;
                // Give power to motors and turn robot
                this.setMotorOutputs(turn, -turn);
                // Subtract one from angleCorrectionCounter to eventually stop angle correction
                angleCorrectionCounter--;
            } else {
                // If path was reversed, change encoders back to positive/forward and negative/backward
                if (pathIsReversed) {
                    leftMaster.setSensorPhase(true);
                    rightMaster.setSensorPhase(true);
                }
                // Tell command group that the robot has finished following the path --> move onto next command
                pathFollowingComplete = true;
                // Stop notifier and set motor output to 0.0
                Robot.stopPathFollowing();
            }
        } else {
            // Calculate power outputs (-1.0 to 1.0) for motors based on the current encoder positions
            double left_output = left_follower.calculate(this.getLeftPosition());
            double right_output = right_follower.calculate(this.getRightPosition());

            // If path is reversed, make outputs negative so robot travels backwards
            if (pathIsReversed) {
                left_output *= -1.0;
                right_output *= -1.0;
            }

            // Get difference in angle between gyro and desired angle
            angleDifference = this.getAngleDifference();
            // Translate angleDifference degrees to motor output with algorithm below
            turn = 0.8 * (1.0/80.0) * angleDifference;

            // Combine encoder-based and gyro-based motor outputs to one motor output value
            double leftOutputWithTurn = left_output + turn;
            double rightOutputWithTurn = right_output - turn;

            // Give power to motors based on calculated motor ouputs
            this.setMotorOutputs(leftOutputWithTurn, rightOutputWithTurn);
        }
    }

    /*****************************************************
     * Methods for telling if path following is complete *
     *****************************************************/
    public boolean getPathFollowingStatus() {
        return pathFollowingComplete;
    }
}

