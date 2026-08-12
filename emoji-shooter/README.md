# Emoji Shooter (Tim's Coding Challenge)

## Instructions:

### Part 1
- Run `EmojiShooter.java` (using the "Run Java" play button in the top right)
- Your job is to shoot the target emojis using the eye emoji
- The laser is that line of dots leaving the eye
- Somehow we need to aim that laser at the target emoji
- Right now, your controls don't work unfortunately
- To fix that, find the `controlOutput` method
- There are three parameters to that method: `leftPressed`, `rightPressed`, and `shootPressed`
- The method outputs a `ControlOutput` object which contains an acceleration and boolean for whether to shoot
- Write the logic for the method (don't overthink it, this is the easy part)
- NOTE: the method will remain a static method, also don't change any code outside of the method (unless you are writing a helper method)
- Run your program and shoot those emojis

### Part 2
- In FRC, we like to automate things to increase speed and precision
- To accomplish this in our program, we will be implementing a PID
- Go to line 78 and comment it out
- Go to line 79 and uncomment it
- We are now using the `autoControlOutput` method instead of the `controlOutput` method
- find the `autoControlOutput` method
- write the auto-aiming and shooting code!
- If you haven't taken trigonometry, brush up here: https://www.youtube.com/watch?v=5tp74g4N8EY
- The [java Math library](https://www.w3schools.com/java/java_math.asp) has all the trigonometric functions you could need!
- If you've never heard of a PID or need a reminder, check this out: https://www.youtube.com/watch?v=tFVAaUcOm4I
- Note that the `PIDController` object does all of the PID math for you
- [`PIDController` documentation](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/pidcontroller.html)
- The constructor for the `PIDController` is on line 43
- You may find the `MathUtil.isNear` method useful
- Your auto shooter should shoot all of the emojis without you even needing to touch the keyboard!
