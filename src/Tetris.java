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