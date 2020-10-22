package com.fz.upload;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        System.out.println("Version v" + Version.versionName);
        int exitCode = new CommandLine(new Command()).execute(args);
        System.exit(exitCode);
    }
}
