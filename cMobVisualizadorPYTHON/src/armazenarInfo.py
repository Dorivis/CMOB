
import random
import json
import grpc
import service_pb2
import service_pb2_grpc

def dictRandom(n):
   dict = {}
   for i in range(n):
     dict[str(random.randrange(n))] = random.randrange(5*n)
   return dict

json_object = json.dumps(dictRandom(15))
json_object = str(json_object)

def run():
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.TesteServiceStub(channel)
  enviar = service_pb2.infoAdd()
  enviar.json = json_object
  response = stub.setInfo(enviar)
  return response

for i in range(50):
    json_object = json.dumps(dictRandom(random.randrange(100)))
    json_object = str(json_object)
    run()