/*
 * Copyright (c) 2010, nmap4j.org
 *
 * All rights reserved.
 *
 * This license covers only the Nmap4j library.  To use this library with
 * Nmap, you must also comply with Nmap's license.  Including Nmap within
 * commercial applications or appliances generally requires the purchase
 * of a commercial Nmap license (see http://nmap.org/book/man-legal.html).
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright 
 *      notice, this list of conditions and the following disclaimer in the 
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the nmap4j.org nor the names of its contributors 
 *      may be used to endorse or promote products derived from this software 
 *      without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jwell56.security.cloud.service.ids.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import lombok.extern.slf4j.Slf4j;

/**
 * A simple class that encapsulates executing NMap.
 * <p>
 * This class executes nmap synchronously.
 * 
 * @author jsvede
 * 
 */

@Slf4j
public class CommandExecutor {

	private String cmd;

	private String dir;

	public CommandExecutor(String cmd, String dir) {
		this.cmd = cmd;
		this.dir = dir;
	}

	/**
	 * This method attempts to execute NMap using the properties supplied when this
	 * object was constructed.
	 * <p>
	 * This method can throw an NMapExecutionException which will be a wrapper
	 * around an IO Exception.
	 * 
	 * @return
	 * @throws NMapExecutionException
	 */
	public String execute() {
		 int retCode = 0;
		try {
			log.info("执行命令：" + cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(dir));

			log.info("============cmd end =========");
			retCode = process.waitFor();
			
			if(retCode == 0) {
				String result = ExecOutput(process);
				log.info("执行结果：" + result);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1";
	}

	/**
	 * Converts the given InputStream to a String. This is how the streams from
	 * executing NMap are converted and later stored in the ExecutionResults.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String convertStream(InputStream is) throws IOException {
		log.info("======= convertStream start ===========");
		String output;
		StringBuffer outputBuffer = new StringBuffer();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(is));
		while ((output = streamReader.readLine()) != null) {
			outputBuffer.append(output);
			outputBuffer.append("\n");
		}
		return outputBuffer.toString();
	}

	private String ExecOutput(Process process) throws IOException {
        String output = "";
        if (process == null) {
            return null;
        } else {
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                output += line;
            }
            input.close();
            ir.close();
            if (output.length() > 0) {
            }
        }
        return output;
    }
}
