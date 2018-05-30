package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CBPComparison {

	protected void backtrack(List<String> leftList, List<String> rightList, List<int[]> VS) {

		System.out.println("Starting BACKTRACK ...");
		System.out.print("Backtrack: ");
		final int N = leftList.size();
		final int M = rightList.size();
		final int MAX = N + M;
		int offset = (2 * MAX + 2) / 2;
		int x = N;
		int y = M;

		String sign = "";
		String text = "";

		for (int D = VS.size() - 1; D >= 0 && (x > 0 || y > 0); D--) {
			int[] V = VS.get(D);

			int k = x - y;

			int kPrev = 0; // previous k
			int xPrev = 0;
			int yPrev = 0;

			// lower < upper -> upward, else leftward
			if (k == -D || (k != D && V[k - 1 + offset] < V[k + 1 + offset])) {
				kPrev = k + 1;
				xPrev = V[kPrev + offset];
				yPrev = xPrev - kPrev;
				if (yPrev >= 0 && yPrev < M) {
					sign = " +";
					text = rightList.get(yPrev);
				}
			} else {
				kPrev = k - 1;
				xPrev = V[kPrev + offset];
				yPrev = xPrev - kPrev;
				if (xPrev >= 0 && xPrev < N) {
					sign = " -";
					text = leftList.get(xPrev);
				}
			}

			while (x > xPrev && y > yPrev) {
				System.out.print("  " + leftList.get(x - 1));
				x = x - 1;
				y = y - 1;
			}
			
			if (D > 0) {
				System.out.print(sign + text);
			}
			
			x = xPrev;
			y = yPrev;
		}
		System.out.println();
	}
	
	public List<String> diff(List<String> leftList, List<String> rightList) {

		final int N = leftList.size();
		final int M = rightList.size();
		final int MAX = N + M;
		int[] V = new int[2 * MAX + 2];
		List<List<String>> paths = new ArrayList<List<String>>();
		for (int i = 0; i < 2 * MAX + 1; i++) {
			paths.add(new ArrayList<String>());
		}
		List<String> path = null;
		// offset is a middle index to shift the k to the middle of the array
		// since array index cannot be negative (less than 0)
		int offset = (2 * MAX + 2) / 2;
		V[1 + offset] = 0;
		int x = 0;
		int y = 0;
		int D = -1;

		int lineNumber = -1;

		List<String> comparisonList = new ArrayList<>();
		String command = "";

		List<int[]> VS = new ArrayList<>();

		for (D = 0; D <= MAX; D++) {
			VS.add(V.clone());
			for (int k = -D; k <= D; k += 2) {
				int lower = V[k - 1 + offset];
				int upper = V[k + 1 + offset];
				// lower < upper -> downward, else rightward
				if (k == -D || (k != D && lower < upper)) {
					System.out.println("--------");
					System.out.println("D = " + D + ", k = " + k + ", x = " + x + ", y = " + y);
					System.out.println("V = " + Arrays.toString(V));
					System.out.println("paths = " + paths);

					x = V[k + 1 + offset];

					path = new ArrayList<String>(paths.get(k + 1 + offset));
					y = x - k;
					if (y > rightList.size()) {
					} else if (y > 0) {
						path.add("+" + rightList.get(y - 1));
						System.out.println("--------");
						System.out.println("D = " + D + ", k = " + k + ", x = " + x + ", y = " + y + ", String = +"
								+ rightList.get(y - 1));
						System.out.println("V = " + Arrays.toString(V));
						System.out.println("paths = " + paths);

						comparisonList.add("+" + rightList.get(y - 1));
					}

				} else {
					x = V[k - 1 + offset] + 1;

					path = new ArrayList<String>(paths.get(k - 1 + offset));
					y = x - k;
					if (x > leftList.size()) {
					} else if (x > 0) {
						path.add("-" + leftList.get(x - 1));
						System.out.println("--------");
						System.out.println("D = " + D + ", k = " + k + ", x = " + x + ", y = " + y + ", String = -"
								+ leftList.get(x - 1));
						System.out.println("V = " + Arrays.toString(V));
						System.out.println("paths = " + paths);
						comparisonList.add("-" + leftList.get(x - 1));
					}
				}
				y = x - k;

				while (x < N && y < M && leftList.get(x).equals(rightList.get(y))) {
					// comparison.addComparisonLine(lineNumber, x, y,
					// leftText.get(x), rightText.get(y));

					// comparisonList.add(" " + leftList.get(x) + " " + " " +
					// rightList.get(y));

					path.add(" " + leftList.get(x));
					System.out.println("--------");
					System.out.println(
							"D = " + D + ", k = " + k + ", x = " + x + ", y = " + y + ", String =  " + leftList.get(x));
					System.out.println("V = " + Arrays.toString(V));
					System.out.println("paths = " + paths);
					comparisonList.add(" " + leftList.get(x));

					x = x + 1;
					y = y + 1;
				}

				if (x == 2 && y == 4 && k == -2) {
					System.out.println();
				}

				V[k + offset] = x;
				paths.set(k + offset, path);
				System.out.println("--------");
				System.out.println("D = " + D + ", k = " + k + ", x = " + x + ", y = " + y);
				System.out.println("V = " + Arrays.toString(V));
				System.out.println("paths = " + paths);

				if (x >= N && y >= M) {

					System.out.println("Comparison List");
					int line = 1;
					for (String item : comparisonList) {
						System.out.println(line + ": " + item);
						line++;
					}

					System.out.println(path);
					backtrack(leftList, rightList, VS);

					return path;
				}
			}

		}
		return path;
	}
}
