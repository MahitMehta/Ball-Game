package com.ballgame

abstract class Rectangle(x: Int, y: Int) : GameObject(x, y) {
	var width = 0
		protected set
	var height = 0
		protected set
	val centerX = x + (width / 2)
	val centerY = y + (height / 2)
}
