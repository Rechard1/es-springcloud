package com.jwell56.security.cloud.common.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author wsg
 * @since 2020/12/3
 */
public class ShellUtil {
    public static String ExecCommand(String command) {
        int retCode = 0;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, null);
            retCode = process.waitFor();
            return ExecOutput(process);
        } catch (Exception e) {
            retCode = -1;
        }
        return null;
    }

    public static String ExecOutput(Process process) throws Exception {
        String output = "";
        if (process == null) {
            return null;
        } else {
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                output += line + "\n";
            }
            input.close();
            ir.close();
            if (output.length() > 0) {
            }
        }
        return output;
    }
}
