package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import pb.Service;

import java.util.ArrayList;
import java.util.List;

public class CodeletJson {
    private double activation;
    private long timestamp;
    private String name;
    private List<MemoryJson> broadcast = new ArrayList<MemoryJson>();
    private List<MemoryJson> inputs = new ArrayList<MemoryJson>();
    private List<MemoryJson> outputs = new ArrayList<MemoryJson>();

    public CodeletJson(Codelet cod) {
        this.activation = cod.getActivation();
        this.timestamp = System.currentTimeMillis();
        this.name = cod.getName();
        for (int i = 0; i < cod.getBroadcast().size(); i++) {
            this.broadcast.add(new MemoryJson(cod.getBroadcast().get(i)));
        }
        for (int i = 0; i < cod.getInputs().size(); i++) {
            this.inputs.add(new MemoryJson(cod.getInputs().get(i)));
        }
        for (int i = 0; i < cod.getOutputs().size(); i++) {
            this.outputs.add(new MemoryJson(cod.getOutputs().get(i)));
        }
    }
    public Service.ICodeletProperties.Builder getICodeletProperties (){
        Service.ICodeletProperties.Builder codelet = Service.ICodeletProperties.newBuilder();
        codelet.setActivation(this.activation);
        codelet.setTimestamp(this.timestamp);
        codelet.setName(this.name);

        for (int i = 0; i < this.broadcast.size();i++){
            codelet.addBroadcast(i, this.broadcast.get(i).getIMemoryProperties());
        }
        for (int i = 0; i < this.inputs.size();i++){
            codelet.addInputs(i, this.inputs.get(i).getIMemoryProperties());
        }
        for (int i = 0; i < this.outputs.size();i++){
            codelet.addInputs(i, this.outputs.get(i).getIMemoryProperties());
        }
        return codelet;
    }
}