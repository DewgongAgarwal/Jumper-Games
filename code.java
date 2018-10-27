# Jumper-Games
Basic Prototype for a game involving platforms, a jumper, obstacles and a shoot
//email id : agdewang@outlook.com

import java.awt.event.*; 
import java.awt.*;
import javax.swing.*;

class Player
{
    double px ,py , gravity , jumpforce;
    int pwidth , pheight, x, y;
    boolean isjumping , isfalling ,ismovingright,ismovingleft;
    public Player()
    {
        px=300; 
        py=400; 
        pwidth=16; 
        pheight =32;
        isjumping = isfalling = ismovingright = ismovingleft = false ;
        gravity = 0; 
        jumpforce = 3.5;
    } 

    public void drawPlayer(Graphics2D g)
    {
        g.setColor(Color.red);
        g.fillRect( (int)px , (int)py , pwidth , pheight );
    }

    public boolean doorCollision( MyMap m)
    {
        for( int y = 0 ; y < m.mapheight ; y++ )
        {
            for ( int x = 0 ; x < m.mapwidth ; x++)
            {
                if( m.map[y][x] != 2 )
                {
                    continue;
                } 
                Rectangle r1=new Rectangle((int)this.px,(int)this.py,this.pwidth,this.pheight);
                Rectangle r2=new Rectangle(x * m.cellwidth , y * m.cellheight , m.cellwidth , m.cellheight );
                if(r1.intersects(r2))
                {
                    this.x = x;
                    this.y = y;
                    return true;
                }
            }
        }
        return false;
    }
    
    public void openDoor(MyMap m, Door doors[])
    {
        int conx = 0, cony = 0;
        if (this.doorCollision(m))
        {
            for (int i = 0; i < doors.length; i ++)
            {
                if (doors[i].x == this.x && doors[i].y == this.y)
                {
                    conx = doors[i].conx;
                    cony = doors[i].cony;
                    break;
                }
            }
            this.px = conx * m.cellwidth + (m.cellwidth - pwidth)/2;
            this.py = cony * m.cellheight;
        }
    }
    
    public boolean bulletCollision (Bullet b)
    {
        Rectangle r1=new Rectangle((int)px,(int)py,pwidth,pheight);
        Rectangle r2=new Rectangle((int)b.ix, (int)b.iy, 5, 10);
        return r1.intersects(r2);
    }
    
    public boolean mapcollision( MyMap m, int px,int py)
    {
        for( int y = 0 ; y < m.mapheight ; y++ )
        {
            for ( int x = 0 ; x < m.mapwidth ; x++)
            {
                if( m.map[y][x] != 1 )
                {
                    continue;
                } 
                Rectangle r1=new Rectangle(px,py,pwidth,pheight);
                Rectangle r2=new Rectangle(x * m.cellwidth , y * m.cellheight , m.cellwidth , m.cellheight );
                if(r1.intersects(r2))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveleft()
    {
        ismovingleft=true;
    }

    public void cancelmoveleft()
    {
        ismovingleft=false;
    }

    public void moveright()
    {
        ismovingright=true;
    }

    public void cancelmoveright()
    {
        ismovingright=false;
    }

    public void startjump()
    {
        if( isfalling == false && isjumping == false )
        {
            isjumping = true;
            gravity = jumpforce;
        }
    }

    public void update(MyMap m)
    {

        if ( isjumping == false && isfalling == false )
        {
            if( mapcollision( m,(int)px,(int)py+1) == false )
            {
                isfalling = true;
                gravity = 0;
            }
        }

        if (ismovingright)
        {
            if (mapcollision(m,(int)px+1,(int)py) == false )
            {
                px += 1;
            }
        }
        if (ismovingleft)
        {
            if ( mapcollision(m,(int)px-1,(int)py) == false)
            {
                px -= 1;
            }
        }

        if ( isfalling == true && isjumping == false )
        {
            for ( int i = 0 ; i < gravity ; i++ )
            {
                if ( mapcollision ( m,(int)px,(int)py+1) == false ) 
                {
                    py += 1;
                }
                else
                {
                    gravity = 0;
                    isfalling = false;
                }
            }
            gravity += .1;
        }

        if ( isjumping == true && isfalling == false )
        {
            for ( int i = 0 ; i < gravity ; i++)
            {
                if ( mapcollision (m,(int)px,(int)py-1) == false )
                {
                    py -= 1;
                }
                else
                {
                    gravity = 0;
                    isfalling = true;
                    isjumping = false;
                }
            }
            if( gravity < 1 ) 
            {
                gravity = 0;
                isfalling = true;
                isjumping = false;
            }
            gravity -= .1;
        }
    }
}

class Bullet
{
    double ix = 9*32, iy = 33;
    double fx, fy;
    Bullet(double fx, double fy)
    {
        this.fx = fx;
        this.fy = fy;
    }
    
    public void update()
    {
        iy += 4;
        ix = iy/(fy) * ( fx - 9*32) + 9*32;
    }
    
    public boolean destroy(MyMap m)
    {
        if (ix > m.cellwidth*m.mapwidth || iy > m.cellheight*m.mapheight)
            return true;
        for( int y = 0 ; y < m.mapheight ; y++ )
        {
            for ( int x = 0 ; x < m.mapwidth ; x++)
            {
                if( m.map[y][x] == 1 )
                {
                    Rectangle r1=new Rectangle((int)ix,(int)iy,5,10);
                    Rectangle r2=new Rectangle(x * m.cellwidth , y * m.cellheight , m.cellwidth , m.cellheight );
                    if(r1.intersects(r2))
                    {
                        return true;
                    }
                }                
            }
        }
        return false;
    }
    
    public void drawBullet(Graphics2D g)
    {
        g.setColor(Color.green);
        g.fillRect((int)this.ix,(int)this.iy,5,10);
    }
}

class Door
{
    int x, y, conx, cony;
    Door(int x, int y, int conx, int cony)
    {
        this.x = x;
        this.y = y;
        this.conx = conx;
        this.cony = cony;
    }
    public int getConX()
    {
        return conx;
    }
    public int getConY()
    {
        return cony;
    }
    
}

class MyMap 
{
    int map[][] =  
        {
            {1,1,1,1,1,1,1,1,1,5,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,0,1,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,1},
            {1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1},
            {1,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1},
            {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,1},
            {1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1},
            {1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
            {1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        };
    int mapwidth ,mapheight , cellwidth, cellheight;

    public MyMap()
    {
        mapwidth = 20; mapheight = 15;
        cellwidth = cellheight = 32;
    }

    public void drawMap(Graphics2D g)
    {
        for( int y = 0 ; y < mapheight ; y++ )
        {
            for ( int x = 0 ; x < mapwidth ; x++)
            {
                if( map[y][x] == 1 )
                {
                    g.setColor(Color.white);
                    g.fillRect( x * cellwidth , y * cellheight , cellwidth , cellheight );
                }
                if( map[y][x] == 2 )
                {
                    g.setColor(Color.yellow);
                    g.fillRect( x * cellwidth , y * cellheight , cellwidth , cellheight );
                }
                if( map[y][x] == 5 )
                {
                    g.setColor(Color.pink);
                    g.fillRect( x * cellwidth , y * cellheight , cellwidth , cellheight );
                }
            }
        }
    }

}

class GamePanel extends JPanel implements  ActionListener , KeyListener
{
    MyMap obj1;
    Player obj2;
    Timer t;
    Bullet b1;
    Door doors[] = {new Door(1,11,17,3), new Door(17,3,1,11), new Door(17, 10, 5, 1 ), new Door(5, 1, 17, 10 )};
    public GamePanel()
    {  
        setSize(640,480);
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.black);
        obj1=new MyMap();
        obj2=new Player();
        b1 = new Bullet (obj2.px, obj2.py);
        t=new Timer(15,this);
        t.start();
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (!(obj2.bulletCollision(b1)))
        {
            repaint();
            obj2.update(obj1);
            b1.update();
            if (b1.destroy(obj1))
            b1 = new Bullet(obj2.px, obj2.py);
            else
            obj2.bulletCollision(b1);
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D  g2d = (Graphics2D)g;
        obj1.drawMap(g2d);
        b1.drawBullet(g2d);
        obj2.drawPlayer(g2d);
    }

    public void keyTyped(KeyEvent ke)
    {
    }

    public void keyReleased(KeyEvent ke)
    {
        int a=ke.getKeyCode();
        switch(a)
        {
            case KeyEvent.VK_LEFT :
            obj2. cancelmoveleft();
            break;
            case KeyEvent.VK_RIGHT :
            obj2.cancelmoveright();
            break;
            case KeyEvent.VK_UP :
            break;
        }
    } 

    public void keyPressed(KeyEvent ke)
    {
        int a=ke.getKeyCode();
        switch(a)
        {
            case KeyEvent.VK_LEFT :
            obj2.moveleft();
            break;
            case KeyEvent.VK_RIGHT :
            obj2.moveright();
            break;
            case KeyEvent.VK_UP :
            obj2.startjump();
            break;
            case KeyEvent.VK_SPACE :
            obj2.openDoor(obj1, doors);
            break;
        }
    }
}
class GameWindow extends JFrame
{
    public GameWindow()
    {
        getContentPane().add(new GamePanel());
        pack();
    }
}
public class JumpingJack
{
    public static void main()
    {
        GameWindow obj=new GameWindow();
        obj.setSize(650,490);
        obj.setBackground(Color.orange);
        obj.setTitle("Platformer Game");
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.setResizable(false);
    }
}
