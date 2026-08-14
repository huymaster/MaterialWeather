package com.github.huymaster.materialweather.core.engine.node

import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.NodeBundle
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData

class MathNode(data: RestoreData) : Node(data) {
    override val name: Int = R.string.node_math

    private val a = NodeParam.input<Number>(A_KEY, id = data.getParamId(A_KEY))
    private val b = NodeParam.input<Number>(B_KEY, id = data.getParamId(B_KEY))
    private val result = NodeParam.output<Number>(RESULT_KEY, data.getParamId(RESULT_KEY))
    var operation: Operation = Operation.ADD
        private set

    init {
        restore(data)
    }

    override fun getInputs(): Set<NodeParam.Input<*>> = setOf(a, b)
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf(result)

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        val a = context.get<Number>(this.a) ?: skip()
        val b = context.get<Number>(this.b) ?: skip()
        val result = operation(a, b)
        context.set(this.result, result)
    }

    override fun serialize(data: NodeBundle) {
        data.putString(OPERATION_KEY, operation.symbol)
    }

    fun setOperation(operation: Operation) {
        this.operation = operation
    }

    private fun restore(data: RestoreData) {
        val symbol = data.getString(OPERATION_KEY) ?: return
        this.operation = Operation.fromSymbol(symbol) ?: return
    }

    companion object {
        const val A_KEY = "a"
        const val B_KEY = "b"
        const val RESULT_KEY = "result"
        const val OPERATION_KEY = "operation"
    }

    enum class Operation(
        val symbol: String,
        val operation: (Number, Number) -> Number
    ) {
        ADD("+", ::add),
        SUBTRACT("-", ::subtract),
        MULTIPLY("*", ::multiply),
        DIVIDE("/", ::divide),
        DIVIDE_REMAINDER("%", ::divideRemainder);

        operator fun invoke(a: Number, b: Number): Number = operation(a, b)

        companion object {
            private val SYMBOL_MAP = entries.associateBy { it.symbol }

            fun fromSymbol(symbol: String): Operation? = SYMBOL_MAP[symbol]

            private fun isZero(n: Number): Boolean = when (n) {
                is Double -> n == 0.0
                is Float -> n == 0.0f
                is Long, is Int, is Short, is Byte -> n.toLong() == 0L
                else -> n.toDouble() == 0.0
            }

            private inline fun evaluate(
                a: Number,
                b: Number,
                opDouble: (Double, Double) -> Number,
                opFloat: (Float, Float) -> Number,
                opLong: (Long, Long) -> Number,
                opInt: (Int, Int) -> Number
            ): Number {
                if (a is Double || b is Double) return opDouble(a.toDouble(), b.toDouble())
                if (a is Float || b is Float) return opFloat(a.toFloat(), b.toFloat())
                if (a is Long || b is Long) return opLong(a.toLong(), b.toLong())

                return opInt(a.toInt(), b.toInt())
            }

            private fun add(a: Number, b: Number): Number = evaluate(
                a, b,
                opDouble = { x, y -> x + y },
                opFloat = { x, y -> x + y },
                opLong = { x, y ->
                    val res = x + y
                    if (((x xor res) and (y xor res)) < 0) res.toDouble() else res
                },
                opInt = { x, y ->
                    val res = x.toLong() + y.toLong()
                    if (res in Int.MIN_VALUE..Int.MAX_VALUE) res.toInt() else res
                }
            )

            private fun subtract(a: Number, b: Number): Number = evaluate(
                a, b,
                opDouble = { x, y -> x - y },
                opFloat = { x, y -> x - y },
                opLong = { x, y ->
                    val res = x - y
                    if (((x xor y) and (x xor res)) < 0) res.toDouble() else res
                },
                opInt = { x, y ->
                    val res = x.toLong() - y.toLong()
                    if (res in Int.MIN_VALUE..Int.MAX_VALUE) res.toInt() else res
                }
            )

            private fun multiply(a: Number, b: Number): Number = evaluate(
                a, b,
                opDouble = { x, y -> x * y },
                opFloat = { x, y -> x * y },
                opLong = { x, y ->
                    val res = x * y
                    if (y != 0L && res / y != x) res.toDouble() else res
                },
                opInt = { x, y ->
                    val res = x.toLong() * y.toLong()
                    if (res in Int.MIN_VALUE..Int.MAX_VALUE) res.toInt() else res
                }
            )

            private fun divide(a: Number, b: Number): Number {
                if (isZero(b)) throw NodeException.DivideByZero()
                return evaluate(
                    a, b,
                    opDouble = { x, y -> x / y },
                    opFloat = { x, y -> x / y },
                    opLong = { x, y -> x / y },
                    opInt = { x, y ->
                        if (x == Int.MIN_VALUE && y == -1) x.toLong() / y.toLong()
                        else x / y
                    }
                )
            }

            private fun divideRemainder(a: Number, b: Number): Number {
                if (isZero(b)) throw NodeException.DivideByZero()
                return evaluate(
                    a, b,
                    opDouble = { x, y -> x % y },
                    opFloat = { x, y -> x % y },
                    opLong = { x, y -> x % y },
                    opInt = { x, y -> x % y }
                )
            }
        }
    }
}