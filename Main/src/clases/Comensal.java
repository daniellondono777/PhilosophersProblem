package clases;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Comensal extends Thread{

	private int id; 
	private ArrayList<Cubierto> cubiertos; 
	private Fregadero fregadero; 
	private Mesa mesa; 
	// private int nPlatos; // Platos por comensal
	private Object lockCubiertos; // Monitor para que espera por sus cubiertos 
	
	private int platosComidos; 

	public Comensal(int id, Mesa mesa, Fregadero fregadero) 
	{
		this.id = id; 
		this.cubiertos = new ArrayList<Cubierto>(2);

		this.mesa = mesa; 
		this.fregadero = fregadero; 

		// this.nPlatos = mesa.getNumPlatos()/mesa.getNumComensales(); 
		this.lockCubiertos = new Object();
		this.platosComidos = 0; // Platos que ha comido el comensal

	}

	public int getID() 
	{
		return id; 

	}


	@Override
	public void run() 
	{
		int cursos = mesa.getNumPlatos(); // Mientras el numero de platos sea positivo, coma. 

		while(cursos > 0) 
		{
			Cubierto t1 = null; 
			Cubierto t2 = null; 

			// Coge cubiertos, verifica que tenga uno de cada tipo
			t1 = obtenerCubierto(mesa, "T1");
			t2 = obtenerCubierto(mesa, "T2");
			System.out.println("     [+] Comensal con ID " + this.id + " tiene cubiertos T1 y T2");
			System.out.println("..."); 

			// Comer 
			try 
			{
				comer(t1, t2, mesa);
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}

			// Poner los cubiertos en el fregadero despues de comer
			pasarCubiertoAlFregadero(fregadero, t1);
			pasarCubiertoAlFregadero(fregadero, t2);

			// El thread genera un numero entero aleatorio entre 1 y 3 cada vez que se come un plato
			int min = 1000;
			int max = 3000;

			int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
			try 
			{
				Thread.sleep(random_int);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void comer(Cubierto t1, Cubierto t2, Mesa m) throws Exception, InterruptedException 
	{
		if(tieneCubiertosParaComer()) // Solo puede comer si tiene cubiertos
		{
			this.platosComidos++;
			mesa.pasarPlato(); // Tiene que usar un plato para comer, lo saca del array de platos
			System.out.println("          [+] El comensal con ID " + this.id + " esta comiendo... ");
			Thread.sleep( ThreadLocalRandom.current().nextInt(3000,5000) ); // Come por un tiempo aleatorio entre 3 y 5 segundos
			System.out.println("          [+] Comensal con ID " + this.id + " comio satisfactoriamente.");
			System.out.println("          [+] Comensal con ID " + this.id + " ha comido: " + this.platosComidos + " platos."); 
			System.out.println("");
			Thread.sleep(5000);
		}
		else 
		{
			// Si no tiene cubiertos para comer, los libera y espera
			wait();
		}

	}

	/*
	 * Verifica que tenga cubiertos tipo 1 y tipo 2
	 */
	public boolean tieneCubiertosParaComer() 
	{
		boolean t1 = false; 
		boolean t2 = false; 

		for(Cubierto c: cubiertos) 
		{
			if(c.getTipo() == "T1") 
			{
				t1 = true; 
			}

		}
		for(Cubierto c: cubiertos) 
		{
			if(c.getTipo() == "T2") 
			{
				t2 = true; 
			}

		}

		return t1 = t2; 
	}

	/*
	 * Verifica que tenga cubierto por tipo
	 */
	public boolean yaTieneCubiertoTipo(String tipo) 
	{
		boolean res = false; 
		for(Cubierto c: cubiertos)
		{
			if(c.getTipo() == tipo) 
			{
				res = true; 
			}
		}

		return res; 
	}

	public Cubierto obtenerCubierto(Mesa mesa, String tipo) 
	{	
		Cubierto c = null; 
		try 
		{
			if(tieneCubiertosParaComer() == false) 
			{
				c = mesa.retirarCubiertoPasivo(tipo);
				if(yaTieneCubiertoTipo(c.getTipo()) == false) 
				{
					cubiertos.add(c); // Si no tiene, lo agrega a sus cubiertos
				}
				else 
				{
					mesa.almacenarPasivo(c); // Si no le sirve el que tiene, lo pone de vuelta y espera (espera pasiva)
				}
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}

		return c; 

	}

	public void pasarCubiertoAlFregadero(Fregadero f, Cubierto c) 
	{
		f.almacenarSemiActivamente(c);
	}


}
