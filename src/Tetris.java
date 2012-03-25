import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tetris extends Applet implements Runnable{
	Thread clock;
	
	Image off; // 메모리 상의 가상화면
	Graphics offG; //가상화면 출력용 그래픽 컨텍스트
	
	Random r;
	
	boolean[][] map; //게임 내의 각 칸의 상태를 나타내는 배열
	Color[][] colorMap; //게임 내의 각 칸의 색을 저장한 배열
	Color[] colorType; //7종류의 블록의 색상을 저장하는 배열
	
	int blockType; //떨어지는 블록의 종류
	int[] blockX; //떨어지는 블록을 구성하는 4칸의 X좌표
	int[] blockY; //떨어지는 블록을 구성하는 4칸의 Y좌표
	int blockPos; //떨어지는 블록의 방향(회전을 위해 사용)
	
	int score; //성적
	int delayTime; //블록이 떨어지는 시간
	boolean runGame;
	
	AudioClip turnAudio; //회전할때 소리
	AudioClip deleteAudio; //블록 삭제할때 소리
	AudioClip gameOverAudio; //게임 끝났을때 소리
	
	public void init(){
		//메모리상에 가상화면 만들기
		off=createImage(181,316);
		offG = off.getGraphics();
		offG.setColor(Color.white);
		offG.fillRect(0, 0, 192, 192);
		
		turnAudio = getAudioClip(getCodeBase(), "turn.au"); //사운드 로드
		deleteAudio = getAudioClip(getCodeBase(), "delete.au");
		gameOverAudio = getAudioClip(getCodeBase(), "gameover.au");
		
		map = new boolean[12][21];
		colorMap = new Color[12][21];
		colorType = new Color[7];
		setColorType();
		
		blockX = new int[4];
		blockY = new int[4];
		blockPos = 0;
		
		r = new Random(); // 처음에 내보낼 블록의 종류를 선택(0~7)
		blockType = Math.abs(r.nextInt()%7);
		setBlockXY(blockType); //블록에 종류에 따라 블록의 각 칸을 구성
		
		drawBlock(); // 떨어지는 블록그리기
		drawMap(); // 화면 그리기
		drawGrid(); // 모눈 선(격자) 그리기
		
		score = 0;
		delayTime = 1000;
		runGame = true;
		
		this.requestFocus(); //키보드 포커스 요청
		
		addKeyListener(new MyKeyHandler());
	}
	
	public void setColorType(){ //게임에서 사용하는 7종류의 색을 미리 지정
		colorType[0]= new Color(65, 228, 82); //ㅁ 모양 색상
		colorType[1]= new Color(58, 98, 235); //ㅓ 모양 색상
		colorType[2]= new Color(128, 0, 64); //ㄱ 모양 색상
		colorType[3]= new Color(255, 35, 31); //ㄱ 반대 모양 색상
		colorType[4]= new Color(68, 17, 111); //Z 모양 색상
		colorType[5]= new Color(246, 118, 57); //Z 반대 모양 색상
		colorType[6]= new Color(224, 134, 4); //ㅣ 모양 색상
	}
	
	public void setBlockXY(int type){ //type에 따라 블록을 구성
		switch(type){
		case 0 : //ㅁ 모양
			blockX[0]=5; blockY[0]=0;
			blockX[1]=6; blockY[1]=0;
			blockX[2]=5; blockY[2]=1;
			blockX[3]=6; blockY[3]=1;
			break;
		case 1 : //ㅓ 모양
			blockX[0]=6; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 2 : //ㄱ 모양
			blockX[0]=7; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 3 : //ㄱ 반대 모양
			blockX[0]=5; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=7; blockY[3]=1;
			break;
		case 4 : // Z 모양
			blockX[0]=5; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=6; blockY[3]=2;
			break;
		case 5 : // Z 반대 모양
			blockX[0]=6; blockY[0]=0;
			blockX[1]=5; blockY[1]=1;
			blockX[2]=6; blockY[2]=1;
			blockX[3]=5; blockY[3]=2;
			break;
		case 6 : // ㅣ  모양
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
			clock.start(); //시계 시작
		}
	}
	
	public void paint(Graphics g){
		//가상화면을 실제화면에 출력
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
			
			dropBlock(); //블록을 1행 떨어뜨림
			if(runGame){
				drawBlock(); //떨어지는 블록을 그리기
				drawMap(); //화면 그리기
				drawGrid(); //모눈 선(격자)그리기
			}else{
				drawScore(); //성적 출력
			}
			repaint(); //paint()호출
		}
	}
	
	public void drawScore(){ //성적 출력
		offG.setColor(Color.white);
		offG.fillRect(35, 120, 110, 70);
		offG.setColor(Color.black);
		offG.drawRect(40, 125, 100, 60);
		offG.setColor(Color.red);
		offG.drawString("Game over !", 56, 150);
		offG.setColor(Color.blue);
		offG.drawString("Score : "+score, 56, 170);
	}
	
	public void dropBlock(){ // 블록을 1행 떨어뜨림
		removeBlock(); //현재 블록을 제거
		if(checkDrop()){ //블록을 1행 아래로 이동 가능한 지 확인
			for(int i=0; i<4; i++){
				blockY[i]=blockY[i]+1;
			}
		}
		else{
			drawBlock(); //블록을 그리기
			nextBlock(); //다음 블록을 결정
		}
	}
	
	public void delLine(){ //한 행이 다 찬 경우에 행 삭제
		boolean delOk; //한행이 가득찼는지 여부
		
		for(int row = 20; row>=0; row--){
			delOk= true; 
			for(int col =0; col<12; col++)
			{
				if(!map[col][row]) delOk=false; //map[col][row]이 true가 아니면 delOk는 false;
			}
			if(delOk){
				deleteAudio.play();
				score+=10;//점수계산
				
				if (score<1000){//속도 조절
					delayTime = 1000-score; //점수가 score보다 밑이면 delay시간은 1000-점수
				}
				else{
					delayTime=0; // score가 1000이상이면 delay시간은 0
				}
				for(int delRow=row; delRow>0; delRow--){ //delOk가 true이면 한칸씩 밑으로 내려줌
					for(int delCol=0; delCol<12; delCol++){
						map[delCol][delRow] = map[delCol][delRow-1];
						colorMap[delCol][delRow] = colorMap[delCol][delRow-1];
					}
				}
				for(int i=0; i<12; i++){ //delOk가 true 이면 제일밑에칸을 지워줌
					map[0][i]=false;
					colorMap[0][i]=Color.white; 
				}
			row++;
			}
		}
	}
	
	public void nextBlock() //다음 블록 결정
	{
		blockType=Math.abs(r.nextInt()%7); //블록 랜덤
		blockPos=0;
		delLine();
		setBlockXY(blockType);
		checkGameOver();
	}
	
	public void checkGameOver(){ //게임 종료 여부 조사
		for(int i=0; i<4; i++){
			if(map[blockX[i]][blockY[i]]){ //만약 
				if(runGame){
					gameOverAudio.play();
					runGame = false;
				}
			}
		}
	}
	public void removeBlock()// 블록이 이동하거나 회전하기 위해 현재 블록을 삭제
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

