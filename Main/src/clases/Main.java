package clases;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

public class Main {

	/**
	 * Atributos
	 */
	private int numComensales;
	private int numCubiertosT1; 
	private int numCubiertosT2; 
	private int numPlatos; 
	private int tamFregadero;

	private ArrayList<Comensal> comensales; 

	/*
	 * Constructor
	 */
	public Main() 
	{
		this.comensales = new ArrayList<Comensal>();
	}

	public void leerPropiedades() throws Exception 
	{
		try 
		{
			FileReader fr = new FileReader("./in.properties");
			Properties props = new Properties();
			props.load(fr);

			numComensales = Integer.parseInt(props.getProperty("numComensales"));
			numCubiertosT1 = Integer.parseInt(props.getProperty("numCubiertosT1"));
			numCubiertosT2 = Integer.parseInt(props.getProperty("numCubiertosT2"));
			numPlatos = Integer.parseInt(props.getProperty("numPlatos"));
			tamFregadero = Integer.parseInt(props.getProperty("tamFregadero"));

			System.out.println("[*] Del archivo se obtiene que: ");
			System.out.println("------------------------------------------------");
			System.out.println("");
			System.out.println("[ ] Numero de comensales: " + numComensales);
			System.out.println("[ ] Numero de cubiertos T1: " + numCubiertosT1);
			System.out.println("[ ] Numero de cubiertos T2: " + numCubiertosT2 );
			System.out.println("[ ] Numero de platos: " + numPlatos);
			System.out.println("[ ] Tamano del fregadero: " + tamFregadero);
			System.out.println("");
			System.out.println("------------------------------------------------");
			System.out.println("");
		}

		catch(Exception e) 
		{
			e.printStackTrace();
		}


	}

	public void inicio() 
	{
		Mesa mesa = new Mesa(numCubiertosT1, numCubiertosT2, numPlatos, numComensales);
		Fregadero fregadero = new Fregadero(tamFregadero);
		Lavaplatos lavaplatos = new Lavaplatos(fregadero, mesa);

		for(int i = 1; i <= numComensales; i++) 
		{
			comensales.add(new Comensal(i, mesa, fregadero));
		}


		lavaplatos.start(); // El lavaplatos es un loop infinito


		for(Comensal c: comensales) 
		{	
			c.start();
		}

		try 
		{
			for(Comensal c: comensales) 
			{
				c.join();
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}

		try 
		{
			lavaplatos.join();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}


	}

	public static void main(String[] args) throws Exception 
	{
		Main m = new Main();
		m.leerPropiedades();
		m.inicio(); 

	}


}
