from tkinter import *
from tkinter import ttk
import grpc
import service_pb2
import service_pb2_grpc
import time
from threading import Thread
from google.protobuf.json_format import MessageToJson
from google.protobuf.json_format import MessageToDict
import json
import pprint
import asyncio

class Memories:
  def __init__(self, name, timestamp, I, memories=None):
      self.name = name
      self.timestamp = timestamp
      self.I = I
      self.memories = memories
      if str(memories) != "None":
        self.memories = []
        for i in list(memories):
          if len(i.memories) == 0:
            self.memories.append(Memories(i.name, i.timestamp, i.I))
          else:
            self.memories.append(Memories(i.name, i.timestamp, i.I, i.memories))


  def __repr__(self):
    return "Name: " + str(self.name) + " Timestamp: " + str(self.timestamp) + " I: " +str(self.I) + " memories: " + str(self.memories)

class Codelets:
  def __init__(self,dict):
      self.name = dict["name"]
      if "activation" in dict:
        self.activation = dict["activation"]
      self.timestamp = dict["timestamp"]
      self.broadcast = []
      self.inputs = []
      if "broadcast" in dict:
        for i in dict["broadcast"]:
          self.broadcast.append(Memories(i["name"],i["timestamp"],i["I"]))
      if "inputs" in dict:
        for i in dict["inputs"]:
          self.inputs.append(Memories(i["name"],i["timestamp"],i["I"]))


mem = []

def extrairMemories(memories):
  mem = []
  for i in memories:
    if len(i.memories) == 0:
      mem.append(Memories(i.name,i.timestamp,i.I))
    else:
      mem.append(Memories(i.name, i.timestamp, i.I, i.memories))
  return mem

def extrairCodelets(codelets):
  mem = []
  l = list(codelets)
  for k in l:
    dict_obj = MessageToDict(k)
    mem.append(Codelets(dict_obj))

  return mem

def run():
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.grpcServiceStub(channel)
  empty = service_pb2.Empty()
  response = stub.getMemoriesAndCodelets(empty)
  return response



def update():
  global mem
  global toolMem
  res = run()
  if toolMem:
    mem = extrairMemories(res.memories)
    memoriesToolbar()
  else:
    mem = extrairCodelets(res.codelets)
    codeletsToolbar()
  janela.after(1, update)


janela = Tk()
toolMem = True

janela.title("TITULO DA JANELA")
janela.geometry("500x500")
toolbar = Frame(janela)

treeView = ttk.Treeview(janela)
treeView['columns'] = ("Memories")
treeView.column("#0", width=0, minwidth=0)
treeView.column("Memories",anchor=W, width=250)

treeView.heading("Memories", text="Memories",anchor=W)



def getFilho(memoria, iid, parent):
  oriIid = iid
  idx = 0
  for i in memoria:
    m = str(i.name) + ":" + str(i.I)
    treeView.insert(parent='', index='end', iid=iid, text="Child", values=(m))
    treeView.move(str(iid), str(parent), str(idx))
    idx = idx+1
    iid = iid+1
    if i.memories!=None:
      iid = getFilho(i.memories, iid, idx)
  return iid
def memoriesToolbar():
  global toolMem
  global mem
  treeView.delete(*treeView.get_children())
  if not toolMem:
    res = run()
    mem = extrairMemories(res.memories)
  toolMem = True
  iid = 0

  for i in mem:
    m = str(i.name) + ":" + str(i.I)
    treeView.insert(parent='',index='end',iid=iid, text="Parent", values=(m))
    iid = iid + 1
    if i.memories !=None:
      iid = getFilho(i.memories,iid, iid-1)
def codeletsToolbar():
  global toolMem
  global mem
  treeView.delete(*treeView.get_children())
  if toolMem:
    res = run()
    mem = extrairCodelets(res.codelets)
  toolMem = False
  iid = 0
  for i in mem:
    treeView.insert(parent='', index='end', iid=iid, text="Parent", values=(i.name))
    iid = iid + 1
    if len(i.broadcast) > 0:
      iid = getFilho(i.broadcast, iid, iid-1)
    if len(i.inputs) > 0:
      iid = getFilho(i.inputs, iid, iid-1)

def sendInfo(jsonDict):
  json_object = json.dumps(jsonDict)
  json_object = str(json_object)
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.TesteServiceStub(channel)
  enviar = service_pb2.infoAdd()
  enviar.json = json_object
  response = stub.setInfo(enviar)
  return response

def getInfo(index):
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.TesteServiceStub(channel)
  A = service_pb2.indexInfo()
  A.index = index
  response = stub.getInfo(A)
  return response.response.json


'''
import ast
indexAt = []
final = ''
d = {}
while(final.lower() != 'f'):
  if len(indexAt) == 0:
    data = input("  Digite uma chave-valor \nn para novo consunto de valores \ne para enviar para o servidor \nf para finalizar: ").strip().split(' ')
  else:

    data = input(
      f"  Digite uma chave-valor \nn para novo consunto de valores \ne para enviar para o servidor \ni para pegar alguma informação no servidor entre {indexAt}:\nf para finalizar: ").strip().split(' ')
  if len(data) == 2:
    d[data[0]] = data[1]
  elif data[0] == 'n':
    d = {}
  elif data[0] == 'e':
    r = sendInfo(d)
    indexAt.append(str(r)[0: len(str(r))-2].split()[-1])
  elif data[0] == 'i':
    a = input("Digite a opção desejada: ")
    print(ast.literal_eval(getInfo(int(a))))
  elif data[0] == 'f':
    final = 'f'
#print(ast.literal_eval(getInfo(i)))

'''

'''
toolbar = Frame(janela)
toolbar.pack(side=TOP, fill=X)
treeView.pack(side=TOP,anchor=NW,pady=20)

textIndex = Text(janela, height = 1, width=10)
textIndex.place(x=40, y=300)
Label(janela, text="Index").place(x=0, y=300)



memoriesbt = Button(
  toolbar,
  relief=FLAT,
  compound=LEFT,
  text="Memories",
  command=memoriesToolbar)
memoriesbt.pack(side=LEFT, padx=0, pady=0)

codeletsbt = Button(
  toolbar,
  text="Codelets",
  compound=LEFT,
  command=codeletsToolbar,
  relief=FLAT)
codeletsbt.pack(side=LEFT, padx=0, pady=0)

janela.after(1,update)



janela.mainloop()
'''


def t1():
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.grpcServiceStub(channel)
  empty = service_pb2.Empty()
  response = stub.getMemories(empty)
  return response

A = []
def t2():
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.grpcServiceStub(channel)
  empty = service_pb2.Empty()
  response = stub.getCodelets(empty)
  return response


async def t3(A):
  channel = grpc.insecure_channel('localhost:11111')
  stub = service_pb2_grpc.grpcServiceStub(channel)
  response = stub.sendCodelet(A)
  async for _response in response:
    print(_response)






def create_memory(name='', activation=None, timestamp=None, I=None, memories=None):
  if activation == None:
      if timestamp == None:
        if I==None:
          if memories==None:
            return service_pb2.IMemoryProperties(name=name)
          else:
            return service_pb2.IMemoryProperties(name=name, memories=memories)
        elif memories==None:
          return service_pb2.IMemoryProperties(name=name, I=I)
        else:
          return service_pb2.IMemoryProperties(name=name, I=I, memories=memories)
      elif I==None:
          if memories==None:
            return service_pb2.IMemoryProperties(name=name, timestamp=timestamp)
          else:
            return service_pb2.IMemoryProperties(name=name, timestamp=timestamp,memories=memories)
      elif memories==None:
          return service_pb2.IMemoryProperties(name=name,timestamp=timestamp, I=I)
      else:
          return service_pb2.IMemoryProperties(name=name,timestamp=timestamp, I=I, memories=memories)
  elif timestamp == None:
        if I==None:
          if memories==None:
            return service_pb2.IMemoryProperties(name=name, activation=activation)
          else:
            return service_pb2.IMemoryProperties(name=name, activation=activation, memories=memories)
        elif memories==None:
          return service_pb2.IMemoryProperties(name=name, activation=activation, I=I)
        else:
          return service_pb2.IMemoryProperties(name=name, activation=activation, I=I, memories=memories)
  elif I==None:
    if memories==None:
      return service_pb2.IMemoryProperties(name=name, activation=activation, timestamp=timestamp)
    else:
      return service_pb2.IMemoryProperties(name=name, activation=activation, timestamp=timestamp,memories=memories)
  elif memories==None:
    return service_pb2.IMemoryProperties(name=name, activation=activation,timestamp=timestamp, I=I)
  else:
    return service_pb2.IMemoryProperties(name=name, activation=activation,timestamp=timestamp, I=I, memories=memories)






A = t2()
#print(t1())
print('-------------------------------------')
print(type(A))
t3(A)

