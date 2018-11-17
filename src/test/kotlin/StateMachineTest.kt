import org.mechdancer.dataflow.external.statemachine.machines.linearStateMachine

/** 测试状态机 */
fun main(args: Array<String>) {
	val startWith = linearStateMachine<Int> {
		once { println(it); it + 1 }
		loop(3) { println(it); it * 2 }
		once { println(it); it }
		forever { it }
	}

	startWith(3)
	while (true);
}
