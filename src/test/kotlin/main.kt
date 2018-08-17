import org.mechdancer.dataflow.core.*

fun main(args: Array<String>) {
	val source = broadcast<Int>("源")
	val action = action { x: Int -> println(x) }
	val bridge1 = transform(options = executableOptions(1)) { x: Int -> x - 1 }
	val bridge2 = transform { x: Int -> -x }
	val begin = System.currentTimeMillis()
	val link = link(source, bridge1) { it > 100 }
	link(source, bridge2, linkOptions(100))
	source - { it > 0 } - { println(if (it) "+" else "-") }
	bridge1 linkTo source
	bridge2 linkTo source
	source linkTo { println(link.count / (System.currentTimeMillis() - begin)) }
	source post 100
	while (true) {
		readLine()
		println("收到: ${source.receive()}")
	}
}
