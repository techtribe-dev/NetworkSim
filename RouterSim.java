import java.util.ArrayList; 
import java.util.Random;

interface PhyEth {
  public void setLinkUp(); 
  public void setLinkDown();
  public String getLinkStatus();
}

class EthPort implements PhyEth {
    private enum LinkState{UP, DOWN} LinkState mLink;
    private enum TypeComm{HALF_DUPLEX, FULL_DUPLEX} TypeComm mComm;
    private enum TypeBandwidth{GIGABIT, FAST_ETHERNET} TypeBandwidth mBand;
    private String mEthName;
    private String mMAC;
    private String mIPv4;

    public EthPort(){
        mEthName = "eth0";
        mMAC = "aa:bb:cc:dd:ee:00:66";
        mComm = TypeComm.HALF_DUPLEX;
        mBand = TypeBandwidth.FAST_ETHERNET;
        mLink = LinkState.DOWN;
    }
    
    public EthPort(String name, String typeC, String typeB){
        mEthName = name;
        mMAC = "aa:bb:cc:dd:ee:00:66";
        mComm = (typeC.equals("HALF_DUPLEX"))? TypeComm.HALF_DUPLEX : TypeComm.FULL_DUPLEX;
        mBand = (typeC.equals("GIGABIT"))? TypeBandwidth.GIGABIT : TypeBandwidth.FAST_ETHERNET;
        mLink = LinkState.DOWN;
    }
    

    public EthPort(String name){
        mEthName = name;
    }

    public void setLinkUp(){
        mLink = LinkState.UP;
        System.out.println("Link is UP!");
    }

    public void setLinkDown(){
        mLink = LinkState.DOWN;
        System.out.println("Link is Down");
    }

    public String getLinkStatus(){
        //System.out.println("Link is " + mLink.toString());
        return mLink.toString();
    }
    
    public TypeComm getTC(){
        return mComm;
    }
    
    public TypeBandwidth getTB(){
        return mBand;
    }

    public String getName(){
        return mEthName;
    }

    public void setIPv4(String ip){
        mIPv4 = ip;
    }

}

class UtpWire{    
   private EthPort portLeft;
   private EthPort portRight;

   public UtpWire(EthPort left, EthPort right){
     portLeft = left;
     portRight = right;
   }

   public void changePortLeft(EthPort port){
     portLeft = port;
   }

   public void changePortRight(EthPort port){
     portRight = port;
   }
   
   public EthPort getPortLeft() { return portLeft; }

   public EthPort getPortRight(){ return portRight; }
   
}

class Router {
    private EthPort mInPortWAN;
    private EthPort mOutPortWAN;
    private ArrayList<EthPort> mPortsLAN;
    private String mNameVendor;
    private Integer mID;
    
    public Router(){
        mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT");
        mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT");
        mPortsLAN = new ArrayList<EthPort>();
        for(Integer i = 2; i < 10; i++){
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT");
            mPortsLAN.add(p);
        }
        mID = (new Random()).nextInt(65523) * 123;
    }

    public Router(String vendor){
        mNameVendor = vendor;
        mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT");
        mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT");
        mPortsLAN = new ArrayList<EthPort>();
        for(Integer i = 2; i < 10; i++){
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT");
            mPortsLAN.add(p);
        }
        mID = (new Random()).nextInt(65523) * 123;
    }

    public void displayRoutersInfo(){
        System.out.println("Vendor:" + mNameVendor);
        System.out.println("ID:" + mID);
        System.out.println(mInPortWAN.getName() +"(eth wan IN): " + "link is " + mInPortWAN.getLinkStatus());
        System.out.println(mOutPortWAN.getName() +"(eth wan OUT): " + "link is " + mOutPortWAN.getLinkStatus());
        System.out.println("LAN:");
        for(EthPort ep : mPortsLAN){
            System.out.println(ep.getName() + ": link is " + ep.getLinkStatus());
        }
    }

    public EthPort getInWan(){
        return mInPortWAN;
    }

    public EthPort getOutWan(){
        return mOutPortWAN;
    }

     
    public void connectWAN(UtpWire wire){
         //test cablu daca este conectat in ambele capete
         //negociere viteza de transmisie intre cele doua placi
         //asignare IPv4
         //First Ping Pong
    }

}

class MyMain {
    public static void main(String []args){
        Router r0 = new Router("Microtik");
        Router r1 = new Router("Microtik");
        UtpWire wire1 = new UtpWire(r0.getInWan(), r1.getOutWan());
        r0.displayRoutersInfo();

    }
}