package clases;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Lavaplatos extends Thread{

	private Fregadero fregadero;
	private Mesa mesa; 

	private ArrayList<Cubierto> lavaplatos; 

	public Lavaplatos(Fregadero f, Mesa m) 
	{
		this.lavaplatos = new ArrayList<Cubierto>();
		this.fregadero = f; 
		this.mesa = m; 
	}

	@Override
	public void run() 
	{
		try 
		{	
			while(true) 
			{
				// retira un cubierto del fregadero
				Cubierto c = fregadero.retirarSemiActivamente();
				lavaplatos.add(c);

				// lo lava
				lavarCubierto(c);

				// lo pone en la mesa
				mesa.ponerCubierto(c);
			}

		}

		catch(Exception e) 
		{
			e.printStackTrace();
		}

	}

	public void lavarCubierto(Cubierto c) throws InterruptedException 
	{
		try 
		{
			System.out.println("[*] Se está lavando un cubierto de tipo " + c.getTipo() + "...");
			Thread.sleep( ThreadLocalRandom.current().nextInt(1000,2000) ); // Lava los cubiertos por un tiempo aleatorio entre 1 y 2 segundos
			System.out.println("[*] Cubierto de tipo " + c.getTipo() + " lavado exitosamente.");
		}
		catch(Exception e) 
		{
			System.out.println("");
		}

	}




}
