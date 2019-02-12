/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * <p>The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 *
 * <p>WARNING: While it may look like a good choice to use for your code if
 * you're inexperienced, don't. Unless you know what you are doing, complex code
 * will be much more difficult under this system. Use TimedRobot or
 * Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";

  //sets up a compressor named airmaker
  Compressor airmaker = new Compressor(0);

  CameraServer server;
  
  //setting up left drive motors
  PWMVictorSPX leftFrontDrive = new PWMVictorSPX(1);
  PWMVictorSPX leftRearDrive = new PWMVictorSPX(2);
  SpeedControllerGroup m_leftDrive = new SpeedControllerGroup(leftFrontDrive, leftRearDrive);

  
  //setting up right drive motors
  PWMVictorSPX rightFrontDrive = new PWMVictorSPX(7);
  PWMVictorSPX rightRearDrive = new PWMVictorSPX(8);
  SpeedControllerGroup m_rightDrive = new SpeedControllerGroup(rightFrontDrive, rightRearDrive);
  
  //reverses motors as needed
  public void setInverted()
  {
    /*
    leftFrontDrive.setInverted(true);
    rightFrontDrive.setInverted(true);
    leftRearDrive.setInverted(true);
    rightRearDrive.setInverted(true);
    */
    leftWinch.setInverted(true);
  }
  

  //setting up winch motors & making them one motor to call
  PWMVictorSPX leftWinch = new PWMVictorSPX(3);
  PWMVictorSPX rightWinch = new PWMVictorSPX(6);
  SpeedControllerGroup winch = new SpeedControllerGroup(leftWinch, rightWinch);


  //setting up roller motors & making them into one motor to call
  PWMVictorSPX leftRoller = new PWMVictorSPX(4);
  PWMVictorSPX rightRoller = new PWMVictorSPX(5);


  //creates a Differential drive for the tank drive system to use
  private final DifferentialDrive m_robotDrive
      = new DifferentialDrive(m_rightDrive, m_leftDrive);
  private final XboxController m_stick = new XboxController(0);
  private final Button buttonA = new JoystickButton(m_stick, 1);
  private final Button buttonLB = new JoystickButton(m_stick, 5);
  private final Button buttonRB = new JoystickButton(m_stick, 6);
  private final Button buttonStart = new JoystickButton(m_stick, 8);
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //cargo frame eject & retract
  private final DoubleSolenoid cargoFrame = new DoubleSolenoid(0, 1);

  //climb cylinders eject & retract
  private final DoubleSolenoid climbCylinders = new DoubleSolenoid(2, 3);

  //climb wings cylinders eject & retract
  private final DoubleSolenoid climbWings = new DoubleSolenoid(4, 5);

  //shooter eject & retract
  private final DoubleSolenoid panelShooter = new DoubleSolenoid(6,7);

  public Robot() {
    m_robotDrive.setExpiration(0.1);
  }

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto modes", m_chooser);
    server = CameraServer.getInstance();
    server.startAutomaticCapture("cam0", 0);

    //inverts certain motors
    setInverted();

    cargoFrame.set(Value.kForward);
    climbCylinders.set(Value.kReverse);
    climbWings.set(Value.kReverse);
    panelShooter.set(Value.kReverse);

    //starts compressor named airmaker on closed loop control
    airmaker.setClosedLoopControl(true);

  }

  boolean toggleAOn = false;
  boolean toggleAPressed = false;

  boolean toggleBOn = false;
  boolean toggleBPressed = false;

  boolean toggleYOn = false;
  boolean toggleYPressed = false;

  boolean toggleXOn = false;
  boolean toggleXPressed = false;

  boolean toggleStartOn = false;
  boolean toggleStartPressed = false;

  boolean toggleRBOn = false;
  boolean toggleRBPressed = false;

  public void updateRBToggle()
  {
      if(m_stick.getRawButton(6)){
        System.out.print("Button: RB pressed");
          if(!toggleRBPressed){
              toggleRBOn = !toggleRBOn;
              toggleRBPressed = true;
          }
      }else{
          toggleRBPressed = false;
      }
  }
  
public void updateAToggle()
    {
        if(m_stick.getRawButton(1)){
          System.out.print("Button: A pressed");
            if(!toggleAPressed){
                toggleAOn = !toggleAOn;
                toggleAPressed = true;
            }
        }else{
            toggleAPressed = false;
        }
    }
    
    public void updateBToggle()
    {
        if(m_stick.getRawButton(2)){
          System.out.print("Button: B pressed");
            if(!toggleBPressed){
                toggleBOn = !toggleBOn;
                toggleBPressed = true;
            }
        }else{
            toggleBPressed = false;
        }
    }
    
    public void updateYToggle()
    {
        if(m_stick.getRawButton(4)){
          System.out.print("Button: Y pressed");
            if(!toggleYPressed){
                toggleYOn = !toggleYOn;
                toggleYPressed = true;
            }
        }else{
            toggleAPressed = false;
        }
    }
    public void updateXToggle()
    {
        if(m_stick.getRawButton(3)){
          System.out.print("Button: X pressed");
            if(!toggleXPressed){
                toggleXOn = !toggleXOn;
                toggleXPressed = true;
            }
        }else{
            toggleXPressed = false;
        }
    }
    public void updateStartToggle()
    {
        if(m_stick.getRawButton(8)){
          System.out.print("Button: Start pressed");
            if(!toggleStartPressed){
                toggleStartOn = !toggleStartOn;
                toggleStartPressed = true;
            }
        }else{
            toggleStartPressed = false;
        }
    }

  /*
      public void teleopPeriodic(){
        updateToggle();

        if(toggleOn){
            // Do something when toggled on
        }else{
            // Do something when toggled off
        }

    
     // JoystickButton[] gamepadButton = new JoystickButton[1];
    }

    
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the if-else structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   *
   * <p>If you wanted to run a similar autonomous mode with an TimedRobot
   * you would write:
   *
   * <blockquote><pre>{@code
   * Timer timer = new Timer();
   *
   * // This function is run once each time the robot enters autonomous mode
   * public void autonomousInit() {
   *     timer.reset();
   *     timer.start();
   * }
   *
   * // This function is called periodically during autonomous
   * public void autonomousPeriodic() {
   * // Drive for 2 seconds
   *     if (timer.get() < 2.0) {
   *         myRobot.drive(-0.5, 0.0); // drive forwards half speed
   *     } else if (timer.get() < 5.0) {
   *         myRobot.drive(-1.0, 0.0); // drive forwards full speed
   *     } else {
   *         myRobot.drive(0.0, 0.0); // stop robot
   *     }
   * }
   * }</pre></blockquote>
   */
  @Override
  public void autonomous() {
    String autoSelected = m_chooser.getSelected();
    // String autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + autoSelected);

    // MotorSafety improves safety when motors are updated in loops
    // but is disabled here because motor updates are not looped in
    // this autonomous mode.
    m_robotDrive.setSafetyEnabled(false);

    switch (autoSelected) {
      case kCustomAuto:
        // Spin at half speed for two seconds
        m_robotDrive.arcadeDrive(0.0, 0.5);
        Timer.delay(2.0);

        // Stop robot
        m_robotDrive.arcadeDrive(0.0, 0.0);
        break;
      case kDefaultAuto:
      default:
        // Drive forwards for two seconds
        m_robotDrive.arcadeDrive(-0.5, 0.0);
        Timer.delay(2.0);

        // Stop robot
        m_robotDrive.arcadeDrive(0.0, 0.0);
        break;
    }
  }



  @Override
  public void operatorControl() {
    m_robotDrive.setSafetyEnabled(true);
    while (isOperatorControl() && isEnabled()) {
      // Drive tank style with an xbox controller
      m_robotDrive.tankDrive(m_stick.getY(Hand.kRight), m_stick.getY(Hand.kLeft));

      // The motors will be updated every 5ms
      Timer.delay(0.005);

      //winch Up

      
      updateAToggle();
      updateXToggle();
      updateYToggle();
      updateBToggle();
      updateStartToggle();

      
      //gets Xbox controller trigger axisk  
      if(toggleXOn){
        winch.set(m_stick.getTriggerAxis(Hand.kRight));
      }else{
        winch.set(-m_stick.getTriggerAxis(Hand.kRight));
      }

      if(toggleBOn)
      {
        leftRoller.set(.5);
        rightRoller.set(-.5);
      }else{
        leftRoller.set(0);
        rightRoller.set(0);
      }
      /*
      if(toggleAOn){
        cargoFrame.set(Value.kReverse);
      }else{
        cargoFrame.set(Value.kForward);
      }
      */
      if(m_stick.getAButtonPressed())
      {
        cargoFrame.set(Value.kReverse);
      }else if(m_stick.getAButtonReleased()){
        cargoFrame.set(Value.kForward);
      }
      




      if(toggleRBOn){
        panelShooter.set(Value.kForward);
      }else{
        panelShooter.set(Value.kReverse);
      }
      
      //ejects and retracts the cargo-getter frame


      //ejects and retracts climbing cylinder legs
      if(m_stick.getYButtonPressed())
      {
        climbCylinders.set(Value.kForward);
      }else if(m_stick.getYButtonReleased()){
        climbCylinders.set(Value.kReverse);
      }

      /*
      if(m_stick.getRawButton(7))
      {
        //climbWings.set(Value.kForward);
        panelShooter.set(Value.kForward);
      }
      
      if(m_stick.getRawButton(8)) {
        //climbWings.set(Value.kReverse);
        panelShooter.set(Value.kReverse);
      }
      */
      
      if(m_stick.getRawButtonPressed(8) && panelShooter.get() == Value.kForward){
        panelShooter.set(Value.kReverse);
      }
      else{
        panelShooter.set(Value.kForward);
      }

      //ejects and retracts climbing wing retention cylinders
      /*
      if(toggleStartOn)
      {
        climbWings.set(Value.kForward);
      }else{
        climbWings.set(Value.kForward);
      }
      */
      //toggles roller on/off with B
    }
  }

  /**
   * Runs during test mode.
   */
  @Override
  public void test() {
    m_robotDrive.setSafetyEnabled(true);
    while (isOperatorControl() && isEnabled()) {
      // The motors will be updated every 5ms
      Timer.delay(0.005);
    }
  }
}
