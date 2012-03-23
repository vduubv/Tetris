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
		
		turnAudio = getAudioClip(getCodeBase(), "turn.au");
		deleteAudio = getAudioClip(getCodeBase(), "delete.au");
		gameOverAudio = getAudioClip(getCodeBase(), "gameover.au");
		
		map = new boolean[12][21];
		colorMap = new Color[12][21];
		colorType = new Color[7];
		setColorType();
		
		blockX = new int[4];
		blockY = new int[4];
		blockPos = 0;
		
		r = new Random();
		blockType = Math.abs(r.nextInt()%7);
		setBlockXY(blockType);
		
		drawBlock();
		drawMap();
		drawGrid();
		
		score = 0;
		delayTime = 1000;
		runGame = true;
		
		this.requestFocus();
		
		addKeyListener(new MyKeyHandler());
	}
	
}