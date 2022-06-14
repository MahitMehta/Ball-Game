package com.ballgame

import com.ballgame.StateManager.StateKey.HIGH_SCORE
import com.ballgame.WallBarrier.Companion.Side.LEFT
import com.ballgame.WallBarrier.Companion.Side.RIGHT
import java.awt.*
import java.awt.Color.*
import java.awt.Font.TRUETYPE_FONT
import java.awt.Font.createFont
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.event.KeyListener
import java.awt.image.ImageObserver
import java.io.File
import java.io.IOException
import java.lang.System.nanoTime
import java.lang.Thread.sleep
import java.util.*
import javax.sound.sampled.Clip.LOOP_CONTINUOUSLY
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel
import kotlin.math.roundToLong


class Eskiv private constructor() : JPanel(), Runnable {
	private val startTime = nanoTime()
	private var deltaTime = 0.0
	private var previousTime = 0.0
	private lateinit var obstacles: MutableList<GameObject>
	private lateinit var player: Player
	private lateinit var goal: Goal
	private var gameStarted: Boolean
	private var gameLost = false
	private var gameScore = 0
	private var currentLives = 0

	// State
	private val state = StateManager()

	// Audio
	private var audio = AudioManager()

	// Assets
	private val am: AssetManager = AssetManager()
	private val roboto12: Font = createFont(
		TRUETYPE_FONT, File("kotlin/com/ballgame/assets/fonts/Roboto/Roboto-Regular.ttf")
	).deriveFont(12f)
	private val roboto14: Font = createFont(
		TRUETYPE_FONT, File("kotlin/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")
	).deriveFont(14f)
	private val roboto24: Font = createFont(
		TRUETYPE_FONT, File("kotlin/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")
	).deriveFont(24f)
	private val roboto36: Font = createFont(
		TRUETYPE_FONT, File("kotlin/com/ballgame/assets/fonts/Roboto/Roboto-Bold.ttf")
	).deriveFont(36f)
	private var heart: GameObject? = null

	private var currentlyIntersecting = false
	private var lastIntersectingTime: Long = 0 // nanoseconds
	private val intersectingDebounce = 500 // milliseconds


	init {
		val frame = JFrame("Eskiv Game")
		frame.add(this)
		frame.contentPane.background = BLACK
		frame.setSize(WIDTH, HEIGHT + HEADER_TOP)
		frame.setLocationRelativeTo(null)
		frame.isResizable = false
		frame.defaultCloseOperation = EXIT_ON_CLOSE
		frame.isVisible = true
		Thread(this).also(Thread::start)
		gameStarted = false
		frame.addKeyListener(object : KeyListener {
			override fun keyPressed(ke: KeyEvent) {
				// Reset Game
				if (ke.keyCode == VK_R && gameLost) {
					audio.gameMusic?.loop(LOOP_CONTINUOUSLY)
					audio.loseMusic?.stop()
					initGame()
				}
				// Start Game
				if ((ke.keyCode == VK_S) && !gameStarted) {
					audio.introMusic?.stop()
					audio.gameMusic?.loop(LOOP_CONTINUOUSLY)
					gameStarted = true
				}
				if ((ke.keyCode == VK_LEFT) || (ke.keyCode == VK_A)) player.left = true
				if ((ke.keyCode == VK_UP) || (ke.keyCode == VK_W)) player.up = true
				if ((ke.keyCode == VK_RIGHT) || (ke.keyCode == VK_D)) player.right = true
				if ((ke.keyCode == VK_DOWN) || (ke.keyCode == VK_S)) player.down = true
			}

			override fun keyReleased(ke: KeyEvent) {
				if ((ke.keyCode == VK_LEFT) || (ke.keyCode == VK_A)) player.left = false
				if ((ke.keyCode == VK_UP) || (ke.keyCode == VK_W)) player.up = false
				if ((ke.keyCode == VK_RIGHT) || (ke.keyCode == VK_D)) player.right = false
				if ((ke.keyCode == VK_DOWN) || (ke.keyCode == VK_S)) player.down = false
			}

			override fun keyTyped(ke: KeyEvent) {}
		})
		heart = StaticImage(0, 0, 45, 45, am.heartSprites)
		initGame()
	}

	private fun initGame() {
		obstacles = ArrayList()
		player = Player(200, 200, 400.0)
		gameScore = 0
		currentlyIntersecting = false
		lastIntersectingTime = nanoTime()
		currentLives = this.state.lives
		state.refreshJsonBody()
		goal = Goal(100, 100, 15)
		generateRandomBall()
		gameLost = false
	}

	private fun generateRandomBall() {
		val random = Random()
		val size = random.nextInt(6) + 10
		val x = random.nextInt(WIDTH - 100 - size * 2) + 50
		val y = random.nextInt(HEIGHT - 100 - size * 2) + 50
		val velX = random.nextInt(51) + 100
		val velY = random.nextInt(51) + 100
		val b = Fireball(
			x, y, velX.toDouble(), velY.toDouble(), size, am.fireballSprites
		)
		obstacles.add(b)
	}

	private fun displayStartScreen(g2d: Graphics2D) {
		val boxWidth = 300
		val boxHeight = 200
		g2d.color = Color(0.075f, 0.075f, 0.075f, 0.75f)
		g2d.fillRoundRect(
			((WIDTH * 0.5) - (boxWidth * 0.5)).toInt(),
			((HEIGHT * 0.5) - (boxHeight * 0.5)).toInt(),
			boxWidth,
			boxHeight,
			5,
			5
		)
		g2d.color = GREEN
		drawCenteredString(
			g2d, "Welcome to Eskiv!", Rectangle(WIDTH, HEIGHT - 100), roboto24
		)
		g2d.color = WHITE
		drawCenteredString(
			g2d, "Use Arrow Keys or WASD Keys", Rectangle(WIDTH, HEIGHT - 25), roboto12
		)
		drawCenteredString(
			g2d, "to Move the Blue Ball to the Green Goal", Rectangle(WIDTH, HEIGHT + 10), roboto12
		)

		drawCenteredString(
			g2d,
			"and try to avoid any Fireballs (You Have ${state.lives} Lives). ",
			Rectangle(ImageObserver.WIDTH, ImageObserver.HEIGHT + 45),
			roboto12
		)
		g2d.color = GREEN
		drawCenteredString(
			g2d, "Press \"S\" to Begin...", Rectangle(WIDTH, HEIGHT + 125), roboto12
		)
	}

	private fun displayPlayAgain(g2d: Graphics2D) {
		val boxWidth = 300
		val boxHeight = 200
		g2d.color = Color(0.075f, 0.075f, 0.075f, 0.75f)
		g2d.fillRoundRect(
			((WIDTH * 0.5) - (boxWidth * 0.5)).toInt(),
			((HEIGHT * 0.5) - (boxHeight * 0.5)).toInt(),
			boxWidth,
			boxHeight,
			5,
			5
		)
		g2d.color = WHITE
		drawCenteredString(
			g2d, "Game Over.", Rectangle(WIDTH, HEIGHT - 100), roboto24
		)
		val previousScore = state.highScore
		if (gameScore > previousScore) {
			g2d.color = GREEN
			drawCenteredString(
				g2d, "New High Score! Your Scored $gameScore Points.", Rectangle(WIDTH, HEIGHT - 25), roboto14
			)
		} else {
			g2d.color = WHITE
			drawCenteredString(
				g2d, "Your Scored $gameScore Points.", Rectangle(WIDTH, HEIGHT - 25), roboto14
			)
		}
		g2d.color = GREEN
		drawCenteredString(
			g2d, "Press \"R\" to Play Again!", Rectangle(WIDTH, HEIGHT + 100), roboto14
		)
	}

	public override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		val g2d = g as Graphics2D
		g2d.color = BLACK
		g2d.fillRect(0, 0, WIDTH, HEIGHT)
		previousTime = deltaTime
		deltaTime = (nanoTime() - startTime) / 1000000000.0
		try {
			gameLoop(g2d, previousTime)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun run() {
		try {
			while (true) {
				repaint()
				sleep((1000 / FPS).toLong())
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun drawCenteredString(
		g: Graphics, text: String?, rect: Rectangle, font: Font?
	) {
		val metrics = g.getFontMetrics(font)
		g.font = font
		g.drawString(
			text.toString(),
			rect.x + ((rect.width - metrics.stringWidth(text.toString())) / 2),
			rect.y + ((rect.height - metrics.height) / 2) + metrics.ascent
		)
	}

	@Throws(InterruptedException::class, IOException::class)
	fun gameLoop(g2d: Graphics2D, previousTime: Double) {
		// Display FPS
		g2d.font = roboto12
		g2d.color = WHITE
		val currentFPS = 1 / (deltaTime - previousTime)
		g2d.drawString(currentFPS.roundToLong().toString(), 20, 30)
		val heart = heart as StaticImage

		for (i in state.lives downTo 1) {
			if (i > currentLives) continue
			val heartRightMargin = 10
			val heartTopMargin = 5
			heart.setTransformX(((WIDTH - (i * heart.width) - heartRightMargin)).toDouble())
			heart.setTransformY(heartTopMargin.toDouble())
			heart.render(g2d)
		}
		// Score Board
		drawCenteredString(
			g2d, "SCORE: $gameScore", Rectangle(WIDTH, 100), roboto36
		)
		// High Score
		drawCenteredString(
			g2d, "High Score: ${state.highScore}", Rectangle(WIDTH, 200), roboto12
		)
		// Update Obstacles
		obstacles.forEach {
			if (it is WallBarrier) {
				val wb = it
				wb.render(g2d)
				if (wb.shape.intersects(player.shape.bounds2D)) {
					player.velX = 0.0
					if (wb.side === RIGHT) {
						if (player.x > wb.centerX) player.setTransformX(wb.x + wb.width) else player.setTransformX(
							wb.x - player.diameter
						)
					} else if (wb.side === LEFT) {
						if (player.x > wb.centerX) player.setTransformX(wb.x + wb.width) else player.setTransformX(
							wb.x - player.diameter
						)
					}
				}
				if (!gameLost && gameStarted) wb.update(deltaTime, previousTime)
			} else if (it is Fireball) {
				val b = it
				if (b.intersects(player)) {
					if (gameScore > state.highScore) state.updateKeyWithPrimitive(
						HIGH_SCORE, gameScore
					)
					val deltaSinceLastInteraction = (nanoTime() - lastIntersectingTime) / 1000000.0 // milliseconds
					if (!currentlyIntersecting && (deltaSinceLastInteraction >= intersectingDebounce) && (currentLives > 0) && !gameLost) {
						currentLives--
						audio.fireballContactEffect?.microsecondPosition = 0
						audio.fireballContactEffect?.start()
						if (currentLives == 0) {
							audio.gameMusic?.stop()
							audio.loseMusic?.microsecondPosition = 0
							audio.loseMusic?.start()
							gameLost = true
						}
						currentlyIntersecting = true
						lastIntersectingTime = nanoTime()
					}
				} else currentlyIntersecting = false
				b.render(g2d)
				if ((b.x + b.diameter) >= WIDTH) b.velX *= -1
				if (b.x <= 0) b.velX *= -1
//				Constant Accommodates for macOS Header Height
				if ((b.y + b.diameter) >= HEIGHT) b.velY *= -1
				if (b.y <= 0) b.velY *= -1
				if (!gameLost && gameStarted) b.update(deltaTime, previousTime)
			}
		}
		goal.render(g2d)
		player.render(g2d)
		if (!gameLost && gameStarted) player.update(deltaTime, previousTime)
		if (player.x > WIDTH) player.setTransformX(-player.diameter.toDouble())
		if (player.x < -player.diameter) player.setTransformX(WIDTH.toDouble())
		if (player.y > HEIGHT) player.setTransformY(-player.diameter.toDouble())
		if (player.y < -player.diameter) player.setTransformY(HEIGHT.toDouble())
		if (goal.intersects(player)) {
			gameScore += 5
			val random = Random()
			val x = (((random.nextInt(WIDTH - 50 + 1) + 50) - player.diameter)).toDouble()
			val y = (((random.nextInt(HEIGHT - 50 + 1) + 50) - player.diameter)).toDouble()
			goal.setTransform(x, y)
			if (gameScore == 15) {
				val wb = WallBarrier(10, 100, LEFT)
				wb.velY = 100.0
				obstacles.add(wb)
			} else if (gameScore == 35) {
				val wb = WallBarrier(10, 100, RIGHT)
				wb.velY = 150.0
				obstacles.add(wb)
			}
			generateRandomBall()
		}
		if (gameLost) displayPlayAgain(g2d)
		if (!gameStarted) {
			audio.introMusic?.loop(LOOP_CONTINUOUSLY)
			displayStartScreen(g2d)
		}
	}

	companion object {
		const val WIDTH = 750
		const val HEIGHT = 500
		const val HEADER_TOP = 27
		private const val FPS = 144

		@JvmStatic
		fun main(args: Array<String>) {
			try {
				Eskiv()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}
