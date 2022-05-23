package clases;

import java.util.ArrayList;

public class Fregadero {

	private ArrayList<Cubierto> cubiertos; 
	private int tamFregadero; 
	Object lleno, vacio; //locks

	public Fregadero(int tamanho) 
	{
		this.cubiertos = new ArrayList<Cubierto>();
		this.tamFregadero = tamanho; 

		lleno = new Object();
		vacio = new Object();
	}


	// Almacenamiento semiactivo Yield()
	public void almacenarSemiActivamente(Cubierto c) 
	{
		while(cubiertos.size() == tamFregadero) // No se puede agregar, entonces el thread espera semi activamente 
		{
			Thread.yield();
		}

		synchronized(this) // Sincroniza el fregadero para adicionar el cubierto
		{
			cubiertos.add(c);
			synchronized(vacio) 
			{
				vacio.notify();
			}
		}


	}

	// Retirar semiactivo Yield()
	public Cubierto retirarSemiActivamente() 
	{
		Cubierto c; 

		while(cubiertos.size() == 0) // No hay que retirar 
		{
			Thread.yield();
		}
		synchronized(this) // Sincroniza y retira el primero que encuentre 
		{
			c = cubiertos.remove(0);
			return c; 
		}
	}




}
