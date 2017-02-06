package org.semicloud.cas.shared.intensity.oval2;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 烈度模型参数类
 * 
 * @author Semicloud
 *
 */
public class OvalParams {

	// 长轴参数
	private double _la;
	private double _lb;
	private double _lc;
	private double _lr;

	// 短轴参数
	private double _sa;
	private double _sb;
	private double _sc;
	private double _sr;

	// 基底
	private double _base;

	@Override
	public String toString() {
		String lparams = text("la:{0}, lb:{1}, lc:{2}, lr:{3}", _la, _lb, _lc, _lr);
		String sparams = text("sa:{0}, sb:{1}, lc:{2}, sr:{3}", _sa, _sb, _sc, _sr);
		String base = text("base:{0}", _base);
		return lparams + "\n" + sparams + "\n" + base;
	}

	/**
	 * @return the la
	 */
	public double getLa() {
		return _la;
	}

	/**
	 * @param la
	 *            the la to set
	 */
	public void setLa(double la) {
		_la = la;
	}

	/**
	 * @return the lb
	 */
	public double getLb() {
		return _lb;
	}

	/**
	 * @param lb
	 *            the lb to set
	 */
	public void setLb(double lb) {
		_lb = lb;
	}

	/**
	 * @return the lc
	 */
	public double getLc() {
		return _lc;
	}

	/**
	 * @param lc
	 *            the lc to set
	 */
	public void setLc(double lc) {
		_lc = lc;
	}

	/**
	 * @return the lr
	 */
	public double getLr() {
		return _lr;
	}

	/**
	 * @param lr
	 *            the lr to set
	 */
	public void setLr(double lr) {
		_lr = lr;
	}

	/**
	 * @return the sa
	 */
	public double getSa() {
		return _sa;
	}

	/**
	 * @param sa
	 *            the sa to set
	 */
	public void setSa(double sa) {
		_sa = sa;
	}

	/**
	 * @return the sb
	 */
	public double getSb() {
		return _sb;
	}

	/**
	 * @param sb
	 *            the sb to set
	 */
	public void setSb(double sb) {
		_sb = sb;
	}

	/**
	 * @return the sc
	 */
	public double getSc() {
		return _sc;
	}

	/**
	 * @param sc
	 *            the sc to set
	 */
	public void setSc(double sc) {
		_sc = sc;
	}

	/**
	 * @return the sr
	 */
	public double getSr() {
		return _sr;
	}

	/**
	 * @param sr
	 *            the sr to set
	 */
	public void setSr(double sr) {
		_sr = sr;
	}

	/**
	 * @return the base
	 */
	public double getBase() {
		return _base;
	}

	/**
	 * @param base
	 *            the base to set
	 */
	public void setBase(double base) {
		_base = base;
	}

}
