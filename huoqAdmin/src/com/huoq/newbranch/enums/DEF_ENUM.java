package com.huoq.newbranch.enums;
/**
 * 是否默认值
 * 
 * @author yangpeiliang
 *
 */
public enum DEF_ENUM {
	NEGATIVE_ZERO(-1), DEF_ZERO(0), DEF_ONE(1), DEF_TWO(2), DEF_THREE(3), DEF_FOUR(4), DEF_FIVE(5), DEF_SIX(6), DEF_SEVEN(7), DEF_EIGHT(8), DEF_NINE(9), DEF_TEN(10), DEF_TWENTY(20);

	private int nCode;

	private DEF_ENUM(int _nCode) {
		this.nCode = _nCode;
	}

	@Override
	public String toString() {
		return String.valueOf(this.nCode);
	}
    
    public int getnCode() {
        return nCode;
    }
}
