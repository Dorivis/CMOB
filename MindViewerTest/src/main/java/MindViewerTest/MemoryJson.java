package MindViewerTest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import pb.Service;

import java.util.ArrayList;
import java.util.List;

public class MemoryJson {
    private Long timestamp;
    private volatile Double evaluation;
    private volatile Object I;
    private String name;
    private ArrayList<MemoryJson> memories = new ArrayList<MemoryJson>();

    public MemoryJson(Memory memo) {
        timestamp = memo.getTimestamp();
        evaluation = memo.getEvaluation();
        I = memo.getI();
        name = memo.getName();
        if (memo instanceof MemoryContainer) {
            MemoryContainer memoAux = (MemoryContainer) memo;
            List<Memory> memoList = memoAux.getAllMemories();
            for (int i = 0; i < memoList.size(); i++) {
                this.memories.add(new MemoryJson(memoList.get(i)));
            }
        }

    }

    public Service.IMemoryProperties.Builder getIMemoryProperties (){
        Service.IMemoryProperties.Builder memo = Service.IMemoryProperties.newBuilder();
        memo.setName(this.name);
        memo.setTimestamp(this.timestamp);
        memo.setEvaluation(this.evaluation);
        memo.setI((float)Double.parseDouble(this.I.toString()));
        for (int i = 0; i < this.memories.size();i++){
            memo.addMemories(i, this.memories.get(i).getIMemoryProperties());
        }
        return memo;
    }

}