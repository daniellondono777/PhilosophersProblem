package clases;
import java.util.ArrayList;

public class Mesa {

	// Los cubiertos están en el centro de la mesa
	private ArrayList<Cubierto> cubiertos;
	private int nPlatos; // Platos totales 
	private int numComensales; 
	// private int cubsMaximos; 
	Object vacio; // Para los cubiertos
	Object espera; // Para la espera
	private int mitadPlatos; 

	public Mesa(int cubsTipo1, int cubsTipo2, int nPlatos, int numComensales) 
	{
		this.cubiertos = new ArrayList<Cubierto>(); // "En la mesa siempre va a haber espacio para cubiertos una vez lavados"
		
		for(int i = 0; i < cubsTipo1; i++) 
		{
			Cubierto c = new Cubierto("T1"); // Agrega el numero de cubiertos tipo T1 al array especificados en el parametro
			this.cubiertos.add(c);
		}

		for(int i = 0; i < cubsTipo2; i++) 
		{
			Cubierto c = new Cubierto("T2"); // Agrega el numero de cubiertos tipo T2 al array especificados en el parametro
			this.cubiertos.add(c);
		}

		
		this.nPlatos = nPlatos*numComensales; // Numero de platos en total, cuantos platos consume cada comensal.
		this.mitadPlatos = nPlatos*numComensales/2;

		this.numComensales = numComensales; 
		this.vacio = new Object();
		this.espera = new Object();
	}

	public int getNumPlatos() 
	{
		return nPlatos; 
	}

	public int getNumCubsTipo(String tipo) 
	{
		ArrayList<Cubierto> cubs = new ArrayList<>();
		for(int i = 0; i < cubiertos.size(); i++) 
		{
			Cubierto cubI = cubiertos.get(i);
			if( cubI.getTipo() == tipo) 
			{
				cubs.add(cubI);
			}
		}

		return cubs.size(); 
	}

	public int getNumComensales() 
	{
		return numComensales; 
	}


	public void ponerCubierto(Cubierto c) 
	{
		synchronized(this) 
		{
			cubiertos.add(c); // En la mesa siempre hay espacio para nuevos cubiertos una vez lavados
		}
	}
	
	// Espera semiActiva
	public Cubierto retirarCubierto(String tipo) 
	{
		Cubierto c = null; 

		while(cubiertos.size() == 0) 
		{
			Thread.yield(); // Mantiene el Thread pendiente 
		}

		synchronized(this) // Sincroniza la mesa
		{
			for(Cubierto cub: cubiertos) 
			{
				if(cub.getTipo() == tipo) 
				{
					if(cubiertos.remove(cub) == true) 
					{
						c = cub; 
						cubiertos.remove(cub);
						break; // Rompe el ciclo cuando lo encuentre para notificar
					}
				}
			}
			synchronized(vacio) 
			{
				vacio.notify();
			}
		}

		return c; 
	}

	public synchronized void usarPlato() throws InterruptedException 
	{
		if(nPlatos == 0) 
		{
			System.out.println(" (X)(X) Se acabaron los platos! ");
			System.out.println("...");
			while(true)
			{
				wait(5000); // Fin del programa
			}
		}
		else if(nPlatos == mitadPlatos) 
		{	
			Thread.sleep(7000); // Los comensales deben esperar a que todos terminen
			System.out.println("");
			System.out.println(" [%] Llegamos a la mitad del numero platos!");
			System.out.println(" [%] Se esperaron a todos los comensales que faltaban por comer. Resumiendo ...");
			System.out.println("");
			
		}
	
		nPlatos--; 
		System.out.println("(X) Se acaba de pasar un plato!");
		System.out.println("(i) Quedan en total " + this.nPlatos + " platos");
		System.out.println("..."); 
	}
	
	public void pasarPlato() throws Exception 
	{
		synchronized(this) // Sincroniza la mesa para que solo uno pueda usar el numero de platos que es limitado
		{
			usarPlato();
			synchronized(espera) 
			{
				espera.notify();
			}
		}
	}
	
	// Espera pasiva
	public Cubierto retirarCubiertoPasivo(String tipo) 
	{
		boolean continuar = true;
		Cubierto c = null; 
		
		while(continuar) 
		{
			synchronized(this) // Sincroniza la mesa
			{
				for(Cubierto cub: cubiertos) 
				{
					if(cub.getTipo() == tipo) 
					{
						if(cubiertos.remove(cub) == true) 
						{
							c = cub; 
							cubiertos.remove(cub);
							continuar = false; // Si el cubierto existe, entonces sale del loop y lo retira
							break; 
						}
					}
					
				}
			}
			if(continuar) 
			{
				synchronized(vacio) // Si no lo encuentra, entonces hace que esperen
				{
					try 
					{
						vacio.wait();
					}
					catch(Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		synchronized(espera) 
		{
			try 
			{
				espera.notify();
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		return c; 
	}

	public void almacenarPasivo(Cubierto c) 
	{
		boolean continuar = true; 
		while(continuar) 
		{
			synchronized(this) // Se sincroniza la mesa para almacenar un cubierto
			{
				cubiertos.add(c);
				System.out.println("(+) Se situa en la mesa cubierto de tipo: " + c.getTipo());
				continuar = false; 
			}
			
			if(continuar) // Cuando se agregue, sincroniza la espera y la notifica para que otros puedan almacenar
			{
				synchronized(espera) 
				{
					try 
					{
						espera.notify();
					}
					catch(Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}



}
