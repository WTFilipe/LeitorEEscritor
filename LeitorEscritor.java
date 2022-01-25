class Contador {
   int contador;
   
   Contador() {this.contador = 0 ;}
   
   public void incrementar() { this.contador += 1; }
   
   public void setValor(int valor) { this.contador = valor ;}
   
   public int getValor() { return this.contador; }
}
// Monitor que implementa a logica do padrao leitores/escritores
class LE {
  private int leit, escr;  
  
  // Construtor
  LE() { 
     this.leit = 0; //leitores lendo (0 ou mais)
     this.escr = 0; //escritor escrevendo (0 ou 1)
  } 
  
  // Entrada para leitores
  public synchronized void EntraLeitor (int id) {
    try { 
      while (this.escr > 0) {
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.leit++;  //registra que ha mais um leitor lendo
    } catch (InterruptedException e) { }
  }
  
  // Saida para leitores
  public synchronized void SaiLeitor (int id) {
     this.leit--; //registra que um leitor saiu
     if (this.leit == 0) 
           this.notify(); //libera escritor (caso exista escritor bloqueado)
  }
  
  // Entrada para escritores
  public synchronized void EntraEscritor (int id) {
    try { 
      while ((this.leit > 0) || (this.escr > 0)) {
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.escr++; //registra que ha um escritor escrevendo
    } catch (InterruptedException e) { }
  }
  
  // Saida para escritores
  public synchronized void SaiEscritor (int id) {
     this.escr--; //registra que o escritor saiu
     notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
  }
}



//Aplicacao de exemplo--------------------------------------------------------
// Leitor
class Leitor extends Thread {
  int id; //identificador da thread
  Contador contador;
  LE monitor;//objeto monitor para coordenar a lógica de execução das threads

  // Construtor
  Leitor (int id, Contador contador, LE m) {
    this.id = id;
    this.contador = contador;
    this.monitor = m;
  }

  // Método executado pela thread
  public void run () {
    double j=777777777.7, i;
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id);
        
        if(contador.getValor() % 2 == 0) System.out.println ("Sou a thread leitora "+id+" e verifiquei que o valor é par");
        else System.out.println ("Sou a thread leitora "+id+" e verifiquei que o valor é ímpar");
        
        this.monitor.SaiLeitor(this.id);
        sleep(500 * id);
      }
    } catch (Exception e) { return; }
  }
}

//--------------------------------------------------------
// Escritor
class Escritor extends Thread {
  int id; //identificador da thread
  LE monitor; //objeto monitor para coordenar a lógica de execução das threads
  Contador contador;

  // Construtor
  Escritor (int id, Contador contador, LE m) {
    this.id = id;
    this.contador = contador;
    this.monitor = m;
  }

  // Método executado pela thread
  public void run () {
    double j=777777777.7, i;
    try {
      for (;;) {
        this.monitor.EntraEscritor(this.id); 
        
        contador.setValor(this.id);
        System.out.println ("Sou a thread escritora "+id+" e mudei o valor da variável para " + id);
      
        this.monitor.SaiEscritor(this.id); 
        sleep(500 * id);
      }
    } catch (Exception e) { return; }
  }
}

//Leitor e Escritor
class LeitorEEscritor extends Thread {
  int id; //identificador da thread
  LE monitor; //objeto monitor para coordenar a lógica de execução das threads
  Contador contador;
  
  LeitorEEscritor (int id, Contador contador, LE m) {
    this.id = id;
    this.contador = contador;
    this.monitor = m;
  }
  
  public void run () {
    double j=777777777.7, i;
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id);
        
        System.out.println ("Sou a Thread LeitorEEscritor " + id +" e li o valor " + contador.getValor());
        for (i=0; i<100000000; i++) {j=j/2;} //...loop bobo para simbolizar o tempo de leitura
        this.monitor.SaiLeitor(this.id);
        
        this.monitor.EntraEscritor(this.id);
        contador.incrementar();
        System.out.println ("Sou a Thread LeitorEEscritor "+ id + " e adicionei 1 ao valor, que agora vale " + contador.getValor());
        this.monitor.SaiEscritor(this.id);
        sleep(500 * id);
      }
    } catch (Exception e) { return; }
  }
}

//--------------------------------------------------------
// Classe principal
class LeitorEscritor {
  static final int L = 4;
  static final int E = 4;
  static final int LEE = 4;

  public static void main (String[] args) {
    int i;
    LE monitor = new LE();            // Monitor (objeto compartilhado entre leitores e escritores)
    Leitor[] l = new Leitor[L];       // Threads leitores
    Escritor[] e = new Escritor[E];   // Threads escritores
    LeitorEEscritor[] lee = new LeitorEEscritor[LEE];
    
    Contador contador = new Contador();
    
    for (i=0; i<L; i++) {
       l[i] = new Leitor(i+1, contador, monitor);
       l[i].start(); 
    }
    for (i=0; i<E; i++) {
       e[i] = new Escritor(i+1, contador, monitor);
       e[i].start(); 
    }
    for (i=0; i<LEE; i++) {
       lee[i] = new LeitorEEscritor(i+1, contador, monitor);
       lee[i].start(); 
    }
  }
}
