package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import io.grpc.stub.StreamObserver;
import pb.Service;

import java.util.ArrayList;

public class grpcCodeletWriter extends Codelet {
    private Boolean send = Boolean.FALSE;
    private ClientsObservers clientsObservers;

    public grpcCodeletWriter(String name){
        setName(name);
        this.clientsObservers = ClientsObservers.getInstance();
    }

    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        System.out.println("AAAA");

    }

    public StreamObserver<Service.codeletsResponse> sendRec(StreamObserver<Service.codeletsResponse> responseObserver){
        synchronized (this) {
            this.start();
        }
        return null;
        /*  clientsObservers.addObserver(responseObserver);
        StreamObserver<Service.codeletsResponse> request = new StreamObserver<Service.codeletsResponse>() {
            @Override
            public void onNext(Service.codeletsResponse value) {
                clientsObservers.getObservers().forEach(observer -> {observer.onNext(value);});
            }

            @Override
            public void onError(Throwable t) {
                clientsObservers.getObservers().remove(responseObserver);
            }

            @Override
            public void onCompleted() {
                clientsObservers.getObservers().forEach(
                        observer -> {observer.onCompleted();}
                        );
            }
        };
        return request;*/
    }



}
