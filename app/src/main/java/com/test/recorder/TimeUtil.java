package com.test.recorder;

public class TimeUtil {
	/**
	 * �õ�һ����ʽ����ʱ��
	 * 
	 * @param time ʱ�� ����
	 *            
	 * @return ʱ���֣���
	 */
	public static String getFormatTime(long time) {
		time = time/1000;
		long second = time % 60;
		long minute = (time % 3600) / 60;
		long hour = time / 3600;


		String strSecond = ("00" + second).substring(("00" + second).length() - 2);

		String strMinute = ("00" + minute).substring(("00" + minute).length() - 2);

		String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

		return strHour + ":" + strMinute + ":" + strSecond;
	}
}
