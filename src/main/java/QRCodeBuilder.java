/**
 * Title: QRCodeBuilder.class<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2015    <br>
 * Create DateTime: 2015年10月07日 上午10:24 <br>
 *
 * @author Josh Wang
 */
public class QRCodeBuilder {

	private static final int[] POSITION_VALUE = { 127, 65, 93, 93, 93, 65, 127, 0 };
	private static final int[] ALIGNMENT_VALUE = { 31, 17, 21, 17, 31 };

	// 区域标记,数据区flase
	private boolean[][] areaFlag;
	// 黑为true,白为false
	private boolean[][] dataFlag;

	private int version;
	private int size;

	public QRCodeBuilder(int version) {
		this.version = version;
		this.size = 17 + 4 * version;
		areaFlag = new boolean[size][size];
		dataFlag = new boolean[size][size];

		buildPositionDetection();
		buildAlignment();
        buildTiming();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		StringBuilder areaBuilder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				builder.append(dataFlag[i][j] ? '儶' : '口');
				areaBuilder.append(areaFlag[i][j] ? '儶' : '口');
			}
			builder.append('\n');
			areaBuilder.append('\n');
		}
		builder.append("\n\n\n").append(areaBuilder.toString());
		return builder.toString();
	}

	private void buildPositionDetection() {
		int n = size - 1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				boolean flag = (POSITION_VALUE[i] & (1 << j)) > 0 ? true : false;
				// 
				dataFlag[i][j] = flag;
				dataFlag[n - i][j] = flag;
				dataFlag[i][n - j] = flag;
				// 
				areaFlag[i][j] = true;
				areaFlag[n - i][j] = true;
				areaFlag[i][n - j] = true;
			}
		}
	}

	private void buildAlignment() {
		if (version <= 1) {
			return;
		}
		int start = 6;
		int end = 4 * version + 10;
		int alignCount = version / 7 + 2;
		int[] alignArray = new int[alignCount];
		if (alignCount > 2) {
			int d = (end - start) % (alignCount - 1);
			int b = (end - start) / (alignCount - 1);
			if (d != 0 || b % 2 != 0) {
				b = b / 2 * 2;
				double f = 1.0 * (end - start) / (alignCount - 1);
				if (f - b > 0.5) {
					b += 2;
				}
			}
			for (int k = alignCount - 2; k > 0; k--) {
				int r = alignCount - k - 1;
				alignArray[k] = end - r * b;
			}
		}
		alignArray[0] = start;
		alignArray[alignCount - 1] = end;
		for (int i = 0; i < alignCount; i++) {
			for (int j = 0; j < alignCount; j++) {
				if (i == 0 && j == 0 || i == 0 && j == alignCount - 1 || i == alignCount - 1 && j == 0) {
					continue;
				}
				int ax = alignArray[i];
				int ay = alignArray[j];
				for (int ii = -2; ii <= 2; ii++) {
					for (int jj = -2; jj <= 2; jj++) {
						boolean flag = (ALIGNMENT_VALUE[ii + 2] & (1 << (jj + 2))) > 0 ? true : false;
						dataFlag[ax + ii][ay + jj] = flag;
						areaFlag[ax + ii][ay + jj] = true;
					}
				}
			}
		}
	}

	private void buildTiming() {
        boolean flag = true;
		for (int i = 8; i <= size - 9; i++) {
            if (areaFlag[i][6]) {
                flag = false;
            } else {
                dataFlag[i][6] = flag;
                areaFlag[i][6] = true;
                flag = !flag;
            }
		}
        flag = true;
		for (int j = 8; j <= size - 9; j++) {
            if (areaFlag[6][j]) {
                flag = false;
            } else {
                dataFlag[6][j] = flag;
                areaFlag[6][j] = true;
                flag = !flag;
            }
		}
	}

	public static void main(String[] args) {
		System.out.println(new QRCodeBuilder(10));
	}
}
