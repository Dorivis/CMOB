package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;

import java.util.ArrayList;
import java.util.List;

public class MyJson {
    private List<MemoryJson> memories = new ArrayList<MemoryJson>();
    private List<CodeletJson> codelets = new ArrayList<CodeletJson>();

    public MyJson(List<Memory> memories, List<Codelet> cods) {
        for (int i = 0; i < memories.size(); i++) {
            this.getMemories().add(new MemoryJson(memories.get(i)));
        }
        for (int i = 0; i < cods.size(); i++) {
            this.getCodelets().add(new CodeletJson(cods.get(i)));
        }
    }


    public List<MemoryJson> getMemories() {
        return memories;
    }

    public List<CodeletJson> getCodelets() {
        return codelets;
    }
}