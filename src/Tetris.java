import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tetris extends Applet implements Runnable{
	Thread clock;
	
	Image off; // �޸� ���� ����ȭ��
	Graphics offG; //����ȭ�� ��¿� �׷��� ���ؽ�Ʈ
	
	Random r;
	
	boolean[][] map; //���� ���� �� ĭ�� ���¸� ��Ÿ���� �迭
	Color[][] colorMap; //���� ���� �� ĭ�� ���� ������ �迭
	Color[] colorType; //7������ ����� ������ �����ϴ� �迭
	
	int blockType; //�������� ����� ����
	int[] blockX; //�������� ����� �����ϴ� 4ĭ�� X��ǥ
	int[] blockY; //�������� ����� �����ϴ� 4ĭ�� Y��ǥ
	int blockPos; //�������� ����� ����(ȸ���� ���� ���)
	
	int score; //����
	int delayTime; //����� �������� �ð�
	boolean runGame;
	
	AudioClip turnAudio; //ȸ���Ҷ� �Ҹ�
	AudioClip deleteAudio; //��� �����Ҷ� �Ҹ�
	AudioClip gameOverAudio; //���� �������� �Ҹ�
	
	public void init(){
		//�޸𸮻� ����ȭ�� �����
		off=createImage(181,316);
		offG = off.getGraphics();
		offG.setColor(Color.white);
		offG.fillRect(0, 0, 192, 192);
		
		turnAudio = getAudioClip(getCodeBase(), "turn.au"); //���� �ε�
		deleteAudio = getAudioClip(getCodeBase(), "delete.au");
		gameOverAudio = getAudioClip(getCodeBase(), "gameover.au");
		
		map = new boolean[12][21];
		colorMap = new Color[12][21];
		colorType = new Color[7];
		setColorType();
		
		blockX = new int[4];
		blockY = new int[4];
		blockPos = 0;
		
		r = new Random(); // ó���� ������ ����� ������ ����(0~7)
		blockType = Math.abs(r.nextInt()%7);
		setBlockXY(blockType); //��Ͽ� ������ ���� ����� �� ĭ�� ����
		
		drawBlock(); // �������� ��ϱ׸���
		drawMap(); // ȭ�� �׸���
		drawGrid(); // �� ��(����) �׸���
		
		score = 0;
		delayTime = 1000;
		runGame = true;
		
		this.requestFocus(); //Ű���� ��Ŀ�� ��û
		
		addKeyListener(new MyKeyHandler());
	}
	
	public void setColorType(){ //���ӿ��� ����ϴ� 7������ ���� �̸� ����
		colorType[0]= new Color(65, 228, 82); //�� ��� ����
		colorType[1]= new Color(58, 98, 235); //�� ��� ����
		colorType[2]= new Color(128, 0, 64); //�� ��� ����
		colorType[3]= new Color(255, 35, 31); //�� �ݴ� ��� ����
		colorType[4]= new Color(68, 17, 111); //Z ��� ����
		colorType[5]= new Color(246, 118, 57); //Z �ݴ� ��� ����
		colorType[6]= new Color(224, 134, 4); //�� ��� ����
	}
	
	public void setBlockXY(int type){ //type�� ���� ����� ����
		switch(type){
		case 0 : //�� ���
			blockX[0]=5; blockY[0]=0;
			blockX[1]=6; blockY[1]=0;
			blockX[2]=5; blockY[2]=1;
			blockX[3]=6; blockY[3]=1;
			break;
		case 1 : //�� ���
			blockX[0]=6; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 2 : //�� ���
			blockX[0]=7; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 3 : //�� �ݴ� ���
			blockX[0]=5; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 4 : // Z ���
			blockX[0]=5; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=6; blockY[3]=2;
			break;
		case 5 : // Z �ݴ� ���
			blockX[0]=6; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=5; blockY[3]=2;
			break;
		case 6 : // ��  ���
			blockX[0]=4; blockY[0]=0;
			blockX[1]=5; blockY[1]=0;
			blockX[2]=6; blockY[2]=0;
			blockX[3]=7; blockY[3]=0;
			break;
		}
	}
	
	public void start(){
		if(clock ==null){
			clock = new Thread(this);
			clock.start(); //�ð� ����
		}
	}
	
	public void paint(Graphics g){
		//����ȭ���� ����ȭ�鿡 ���
		g.drawImage(off, 0,0,this);
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void run(){
		while(true){
			try{
				clock.sleep(delayTime);
			}catch(InterruptedException ie){}
			
			dropBlock(); //����� 1�� ����߸�
			if(runGame){
				drawBlock(); //�������� ����� �׸���
				drawMap(); //ȭ�� �׸���
				drawGrid(); //�� ��(����)�׸���
			}else{
				drawScore(); //���� ���
			}
			repaint(); //paint()ȣ��
		}
	}
	
	public void drawScore(){ //���� ���
		offG.setColor(Color.white);
		offG.fillRect(35, 120, 110, 70);
		offG.setColor(Color.black);
		offG.drawRect(40, 125, 100, 60);
		offG.setColor(Color.red);
		offG.drawString("Game over !", 56, 150);
		offG.setColor(Color.blue);
		offG.drawString("Score : "+score, 56, 170);
	}
	
	public void dropBlock(){ // ����� 1�� ����߸�
		removeBlock(); //���� ����� ����
		if(checkDrop()){ //����� 1�� �Ʒ��� �̵� ������ �� Ȯ��
			for(int i=0; i<4; i++){
				blockY[i]=blockY[i]+1;
			}
		}
		else{
			drawBlock(); //����� �׸���
			nextBlock(); //���� ����� ����
		}
	}
	
	public void delLine(){ //�� ���� �� �� ��쿡 �� ����
		boolean delOk; //������ ����á���� ����
		
		for(int row = 20; row>=0; row--){
			delOk= true; 
			for(int col =0; col<12; col++)
			{
				if(!map[col][row]) delOk=false; //map[col][row]�� true�� �ƴϸ� delOk�� false;
			}
			if(delOk){
				deleteAudio.play();
				score+=10;//�������
				
				if (score<1000){//�ӵ� ����
					delayTime = 1000-score; //������ score���� ���̸� delay�ð��� 1000-����
				}
				else{
					delayTime=0; // score�� 1000�̻��̸� delay�ð��� 0
				}
				for(int delRow=row; delRow>0; delRow--){ //delOk�� true�̸� ��ĭ�� ������ ������
					for(int delCol=0; delCol<12; delCol++){
						map[delCol][delRow] = map[delCol][delRow-1];
						colorMap[delCol][delRow] = colorMap[delCol][delRow-1];
					}
				}
				for(int i=0; i<12; i++){ //delOk�� true �̸� ���Ϲؿ�ĭ�� ������
					map[0][i]=false;
					colorMap[0][i]=Color.white; 
				}
			row++;
			}
		}
	}
	
	public void nextBlock() //���� ��� ����
	{
		blockType=Math.abs(r.nextInt()%7); //��� ����
		blockPos=0;
		delLine();
		setBlockXY(blockType);
		checkGameOver();
	}
	
	public void checkGameOver(){ //���� ���� ���� ����
		for(int i=0; i<4; i++){
			if(map[blockX[i]][blockY[i]]){ //���� 
				if(runGame){
					gameOverAudio.play();
					runGame = false;
				}
			}
		}
	}
	public void removeBlock()// ����� �̵��ϰų� ȸ���ϱ� ���� ���� ����� ����
	{
		for(int i=0; i<4; i++){
			map[blockX[i]][blockY[i]]=false;
			colorMap[blockX[i]][blockY[i]] = Color.white;
		}
	}
	
	public boolean checkDrop()
	{
		boolean dropOk = true;
		for(int i=0; i<4; i++){
			if((blockY[i]+1)!=21){
				if(map[blockX[i]][blockY[i]+1])dropOk =false;
			}
			else{
				dropOk=false;
			}
		}
		return dropOk;
	}
	
	
	
	
}

