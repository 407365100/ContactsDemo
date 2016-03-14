package com.allen.indexablelist.domain;

import java.io.Serializable;

/** 电话本 */
public class ContactDomain implements Serializable {
	/**索引字母*/
	public String index;
	/** 更新的值 */
	public String update=null;
	/** 名字 */
	public String name;
	/** 称号 */
	public String call;
	/** 电话号码 */
	public String number="null";
	/** 手机号码 */
	public String mobile="null";
	/** mail */
	public String mail;

	@Override
	public String toString() {
		return "ContactDomain [update=" + update + ", name=" + name + ", call="
				+ call + ", number=" + number + ", mobile=" + mobile
				+ ", mail=" + mail + "]";
	}
}
