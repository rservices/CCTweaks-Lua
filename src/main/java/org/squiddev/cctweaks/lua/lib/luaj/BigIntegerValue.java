package org.squiddev.cctweaks.lua.lib.luaj;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.ThreeArgFunction;

import java.math.BigInteger;
import java.util.Random;

public class BigIntegerValue extends LuaValue {
	private static final String NAME = "biginteger";

	private final BigInteger number;
	private LuaTable metatable;

	private BigIntegerValue(BigInteger number, LuaTable metatable) {
		this.number = number;
		this.metatable = metatable;
	}

	@Override
	public int type() {
		return TUSERDATA;
	}

	@Override
	public String typename() {
		return "userdata";
	}

	@Override
	public LuaValue getmetatable() {
		return metatable;
	}

	@Override
	public byte tobyte() {
		return number.byteValue();
	}

	@Override
	public double todouble() {
		return number.doubleValue();
	}

	@Override
	public float tofloat() {
		return number.floatValue();
	}

	@Override
	public int toint() {
		return number.intValue();
	}

	@Override
	public long tolong() {
		return number.longValue();
	}

	@Override
	public short toshort() {
		return number.shortValue();
	}

	@Override
	public LuaValue tonumber() {
		return valueOf(number.doubleValue());
	}

	@Override
	public double optdouble(double def) {
		return number.doubleValue();
	}

	@Override
	public int optint(int def) {
		return number.intValue();
	}

	@Override
	public LuaInteger optinteger(LuaInteger def) {
		return valueOf(number.intValue());
	}

	@Override
	public long optlong(long def) {
		return number.longValue();
	}

	@Override
	public LuaNumber optnumber(LuaNumber def) {
		return valueOf(number.doubleValue());
	}

	@Override
	public int checkint() {
		return number.intValue();
	}

	@Override
	public LuaInteger checkinteger() {
		return valueOf(number.intValue());
	}

	@Override
	public long checklong() {
		return number.longValue();
	}

	@Override
	public LuaNumber checknumber() {
		return valueOf(number.doubleValue());
	}

	@Override
	public LuaNumber checknumber(String msg) {
		return valueOf(number.doubleValue());
	}

	@Override
	public String checkjstring() {
		return number.toString();
	}

	@Override
	public LuaString checkstring() {
		return valueOf(number.toString());
	}

	@Override
	public boolean eq_b(LuaValue luaValue) {
		return this == luaValue || this.comparemt(EQ, luaValue).toboolean();
	}

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof BigIntegerValue && number.equals(((BigIntegerValue) o).number));
	}

	@Override
	public LuaValue eq(LuaValue luaValue) {
		return this == luaValue ? TRUE : this.comparemt(EQ, luaValue);
	}

	public static void setup(LuaValue env) {
		env.rawset(NAME, BigIntegerFunction.makeTable(env));
	}

	private static BigInteger getValue(LuaValue value) {
		if (value instanceof BigIntegerValue) {
			return ((BigIntegerValue) value).number;
		} else if (value.type() == TSTRING) {
			try {
				return new BigInteger(value.toString());
			} catch (NumberFormatException e) {
				throw new LuaError("bad argument: number expected, got " + value.typename());
			}
		} else {
			return BigInteger.valueOf(value.checklong());
		}
	}

	private static class BigIntegerFunction extends ThreeArgFunction {
		private static final String[] META_NAMES = new String[]{
			"unm", "add", "sub", "mul", "mod", "pow", "div", "idiv",
			"band", "bor", "bxor", "shl", "shr", "bnot",
			"eq", "lt", "le",
			"tostring", "tonumber",
		};

		private static final String[] MAIN_NAMES = new String[]{
			"new", "modinv", "gcd", "modpow", "abs", "min", "max",
			"isProbPrime", "nextProbPrime", "newProbPrime"
		};

		private static final int CREATE_INDEX = 19;

		private LuaTable metatable;

		private BigIntegerFunction(LuaTable metatable) {
			this.metatable = metatable;
		}

		@Override
		public LuaValue call(LuaValue left, LuaValue right, LuaValue third) {
			try {
				switch (opcode) {
					case 0: { // unm
						BigInteger leftB = getValue(left);
						return new BigIntegerValue(leftB.negate(), metatable);
					}
					case 1: { // add
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.add(rightNum), metatable);
					}
					case 2: { // sub
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.subtract(rightNum), metatable);
					}
					case 3: { // mul
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.multiply(rightNum), metatable);
					}
					case 4: { // mod
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.remainder(rightNum), metatable);
					}
					case 5: { // pow
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.pow(right.checkint()), metatable);
					}
					case 6:
					case 7: { // div
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.divide(rightNum), metatable);
					}
					case 8: { // band
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.and(rightNum), metatable);
					}
					case 9: { // bor
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.or(rightNum), metatable);
					}
					case 10: { // bxor
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.xor(rightNum), metatable);
					}
					case 11: { // shl
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.shiftLeft(right.checkint()), metatable);
					}
					case 12: { // shr
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.shiftRight(right.checkint()), metatable);
					}
					case 13: { // bnot
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.not(), metatable);
					}
					case 14: { // eq
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.equals(rightNum) ? TRUE : FALSE;
					}
					case 15: { // lt
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.compareTo(rightNum) < 0 ? TRUE : FALSE;
					}
					case 16: { // le
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.compareTo(rightNum) <= 0 ? TRUE : FALSE;
					}
					case 17: { // tostring
						return valueOf(getValue(left).toString());
					}
					case 18: { // tonumber
						return valueOf(getValue(left).doubleValue());
					}
					case 19: { // new
						if (left instanceof BigIntegerValue) {
							return left;
						} else if (left.type() == TSTRING) {
							return new BigIntegerValue(new BigInteger(left.toString()), metatable);
						} else {
							return new BigIntegerValue(BigInteger.valueOf(left.checklong()), metatable);
						}
					}
					case 20: { // modinv
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.modInverse(rightNum), metatable);
					}
					case 21: { // gcd
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.gcd(rightNum), metatable);
					}
					case 22: { // modpow
						BigInteger leftNum = getValue(left), rightNum = getValue(right), thirdNum = getValue(third);
						return new BigIntegerValue(leftNum.modPow(rightNum, thirdNum), metatable);
					}
					case 23: { // abs
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.abs(), metatable);
					}
					case 24: { // min TODO: Varargs version
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.min(rightNum), metatable);
					}
					case 25: { // max
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.max(rightNum), metatable);
					}
					case 26: { // isProbPrime
						BigInteger leftNum = getValue(left);
						int rightProb = right.optint(100);
						return leftNum.isProbablePrime(rightProb) ? TRUE : FALSE;
					}
					case 27: { // nextProbPrime
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.nextProbablePrime(), metatable);
					}
					case 28: { // newProbPrime
						int length = left.checkint();
						Random seed = new Random(right.checkint());
						return new BigIntegerValue(BigInteger.probablePrime(length, seed), metatable);
					}
					default:
						throw new LuaError("No such method " + opcode);
				}
			} catch (ArithmeticException e) {
				// TODO: Handle this more sensibly
				return LuaDouble.NAN;
			}
		}

		private static LuaTable makeTable(LuaValue env) {
			LuaTable meta = new LuaTable(0, META_NAMES.length + 2);
			LuaTable table = new LuaTable(0, META_NAMES.length + MAIN_NAMES.length);

			BigIntegerFunction create = new BigIntegerFunction(meta);
			create.opcode = CREATE_INDEX;
			create.name = "new";
			create.env = env;
			table.rawset("new", create);

			for (int i = 0; i < META_NAMES.length; i++) {
				BigIntegerFunction func = new BigIntegerFunction(meta);
				func.opcode = i;
				func.name = META_NAMES[i];
				func.env = env;
				table.rawset(META_NAMES[i], func);
				meta.rawset("__" + META_NAMES[i], func);
			}

			for (int i = 0; i < MAIN_NAMES.length; i++) {
				BigIntegerFunction func = new BigIntegerFunction(meta);
				func.opcode = i + META_NAMES.length;
				func.name = MAIN_NAMES[i];
				func.env = env;
				table.rawset(MAIN_NAMES[i], func);
			}

			meta.rawset("__index", table);
			meta.rawset("__type", valueOf(NAME));

			return table;
		}
	}
}