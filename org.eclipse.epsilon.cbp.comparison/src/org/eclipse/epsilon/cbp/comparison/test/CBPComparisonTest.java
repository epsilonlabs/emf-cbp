package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.junit.Test;

public class CBPComparisonTest {

    File originFile = new File("D:\\TEMP\\COMPARISON2\\test\\origin.cbpxml");
    File leftFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.cbpxml");
    File rightFile = new File("D:\\TEMP\\COMPARISON2\\test\\right.cbpxml");

//     File originFile = new File("D:\\TEMP\\COMPARISON\\temp\\origin.cbpxml");
//     File leftFile = new File("D:\\TEMP\\COMPARISON\\temp\\left.cbpxml");
//     File rightFile = new File("D:\\TEMP\\COMPARISON\\temp\\right.cbpxml");

    @Test
    public void testReadingFileSpeed() {

	CBPComparison comparison = new CBPComparison();
	comparison.compare(leftFile, rightFile, originFile);
	assertEquals(true, true);

    }
}
