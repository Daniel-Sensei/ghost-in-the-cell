package org.example.controller;

import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.platforms.desktop.DesktopService;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

public class EmbASPManager {
    private static EmbASPManager instance;
    private Handler handler;
    private InputProgram program;


    private EmbASPManager() {
        DesktopService service = new DLV2DesktopService("lib/dlv2.exe");
        handler = new DesktopHandler(service);
        OptionDescriptor option = new OptionDescriptor("-n 1");
        handler.addOption(option);
        program = new ASPInputProgram();
    }

    public static EmbASPManager getInstance() {
        if (instance == null) {
            instance = new EmbASPManager();
        }
        return instance;
    }

    public Handler getHandler() {
        return handler;
    }

    public InputProgram getProgram() {
        return program;
    }


}
