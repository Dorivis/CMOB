/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.util.MindViewer;
import com.google.gson.Gson;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * @author rgudwin
 */
public class Main {
    
    Mind m;
    List<String> infos;
    
    void updateMemoryObject(MemoryObject mo) {
        double value = (double) mo.getI();
        mo.setI(value+0.01);
    }
    
    void updateMemoryContainer(MemoryContainer mc) {
        for (Memory mem : mc.getAllMemories()) {
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryObject")) {
                updateMemoryObject((MemoryObject)mem);
                //System.out.println("Updating subnode");
            }    
        }
    }

    void updateMind() {
        //System.out.println("Updating Mind");
        for (Memory mem : m.getRawMemory().getAllMemoryObjects()) {
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryObject")) {
                updateMemoryObject((MemoryObject)mem);
            }
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryContainer")) {
                updateMemoryContainer((MemoryContainer)mem);
            }
        }
        
    }
    
    public void StartTimer() {
        Timer t = new Timer();
        Main.mainTimerTask tt = new Main.mainTimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 100);
    }

    public void tick() {
        if (m != null) {
            updateMind();
        } else {
            System.out.println("Mind is null");
        }
        //System.out.println("update");
    }
    
    class mainTimerTask extends TimerTask {

        Main wov;
        boolean enabled = true;

        public mainTimerTask(Main wovi) {
            wov = wovi;
        }

        public void run() {
            if (enabled) {
                wov.tick();
            }
        }

        public void setEnabled(boolean value) {
            enabled = value;
        }
    }
    
    
    public Mind prepareMind() {
        m = new Mind();
        m.createCodeletGroup("Sensory");
        m.createCodeletGroup("Perception");
        m.createCodeletGroup("Behavioral");
        m.createCodeletGroup("Motivational");
        m.createCodeletGroup("Motor");
        
        MemoryObject m1 = m.createMemoryObject("M1", 1.12);
        MemoryObject m2 = m.createMemoryObject("M2", 2.32);
        MemoryObject m3 = m.createMemoryObject("M3", 3.44);
        MemoryObject m4 = m.createMemoryObject("M4", 4.52);
        MemoryObject m5 = m.createMemoryObject("M5", 5.12);
        MemoryContainer m6 = m.createMemoryContainer("C1");
        MemoryContainer m7 = m.createMemoryContainer("C2");
        int mc1 = m7.setI(7.55, 0.23);
        int mc2 = m6.setI(6.33, 0.22);
        int mc3 = m6.setI(6.12, 0.13);
        int mc4 = m6.add(m7);
        //System.out.println("Memories: "+mc1+" "+mc2+" "+mc3+" "+mc4);
        
        Codelet c = new TestCodelet("Sensor1");
        c.addInput(m1);
        c.addInput(m2);
        c.addOutput(m3);
        c.addOutput(m4);
        c.addBroadcast(m5);
        m.insertCodelet(c,"Sensory");
        Codelet c2 = new TestCodelet("Motor1");
        c2.addInput(m4);
        c2.addInput(m5);
        c2.addOutput(m6);
        c2.addOutput(m3);
        c2.addBroadcast(m5);
        m.insertCodelet(c2,"Motor");
        
        Codelet mot1 = new TestCodelet("Curiosity");
        mot1.addInput(m7);
        mot1.addOutput(m5);
        m.insertCodelet(mot1,"Motivational");
        Codelet mot2 = new TestCodelet("Fear");
        mot2.addInput(m3);
        mot2.addOutput(m4);
        try {mot2.setActivation(1.0);} catch(Exception e){}
        m.insertCodelet(mot2,"Motivational");
        Codelet mot3 = new TestCodelet("Anger");
        mot3.addInput(m1);
        mot3.addOutput(m2);
        try {mot3.setActivation(0.5);} catch(Exception e){}
        m.insertCodelet(mot3,"Motivational");
        m.start();
        return(m);
    }
    
private void createAndShowGUI(Mind m) {
//        //Create and set up the window.
//        JFrame frame = new JFrame("New Mind Viewer");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        //Create and set up the content pane.
//        MindPanel newContentPane = new MindPanel(m);
//        newContentPane.setOpaque(true); //content panes must be opaque
//        frame.setContentPane(newContentPane);
//
//        //Display the window.
//        frame.pack();
//        frame.setVisible(true);
        MindViewer mv = new MindViewer(m,"MindViewer",m.getCodeletGroupList("Motor"));
        mv.setVisible(true);
    }   

public Main() {
    Mind m = prepareMind();
    infos = new ArrayList<String>();
    //Caso queira ver a interface, descomentar a linha abaixo
    createAndShowGUI(m);


}
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Main mainApp = new Main();
        mainApp.StartTimer();
    	//MindViewer ov = new MindViewer(m, "Mind", new ArrayList<>());
        //ov.setVisible(true);

        /*
        ServiceTeste serviceTeste = new ServiceTeste(mainApp.m, mainApp.infos);
        Server server = ServerBuilder.forPort(11111)
                .addService(serviceTeste)
                .build();

        server.start();
        server.awaitTermination();
        */

        GrpcServer grpcServer = new GrpcServer(mainApp.m);
        grpcServer.start(11111);
        grpcServer.awaitTermination();


      /*  Express app = new Express();
        Gson gson = new Gson();
        CorsOptions corsOptions = new CorsOptions();
        corsOptions.setOrigin("*");
        app.use(Middleware.cors(corsOptions));
        app.get("/", (req, res) -> {
            MyJson myJson = new MyJson(mainApp.m.getRawMemory().getAllMemoryObjects(),mainApp.m.getCodeRack().getAllCodelets());
            res.send(gson.toJson(myJson));
        });
        app.listen(5000);*/

    }
    
}
