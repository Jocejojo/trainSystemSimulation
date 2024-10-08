package assignment_jocelyn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ecs100.UI;

public class DataReader {

	private String fileName;
	
	public DataReader(String name) {
		this.fileName = name;
	}
	
	public List<TrainLine> trainLinesReader() {
		List<TrainLine> trainLines = new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File(this.fileName));
			while(sc.hasNext()) {
				String name = sc.nextLine();				
				TrainLine tl = new TrainLine (name);				
				trainLines.add(tl);
				
//				String[] station = name.split("_", 2);
//				String departure = station[0];
//				String terminal = station[1];
//				UI.println("Train line:" + line);
//				UI.println("Departure: " + departure + "; Terminal: " + terminal);
			}
			sc.close();
			
		}catch(IOException e){
			UI.println("Error: "+e);
		}
		return trainLines;
	}
	
	public Map<Integer, Double> fareReader() {
		Map <Integer, Double> zoneFare = new HashMap<>();
		try {
			Scanner sc = new Scanner(new File(this.fileName));
			sc.nextLine();
			while(sc.hasNextLine()) {		
				String line = sc.nextLine();
				Scanner lineSc = new Scanner (line);
				if(lineSc.hasNextInt()) {
					int zone = lineSc.nextInt();
					double fare = lineSc.nextDouble();
					zoneFare.put(zone, fare);
				}
				lineSc.close();
			}
			sc.close();
			
		}catch(IOException e) {
			UI.println("Error: " + e);
		}
		return zoneFare;
	}
	
	public List<Station> stationReader() {
		List<Station> stations = new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File(this.fileName));
			while(sc.hasNext()) {
				String line = sc.nextLine();
				Scanner lineSc = new Scanner(line);
				String name = lineSc.next();
				int stationZone = lineSc.nextInt();
				double distance = lineSc.nextDouble();
				Station station = new Station (name, stationZone, distance);
				stations.add(station);
				lineSc.close();
//				UI.println("Station: " + station + "; Zone: " + stationZone +"; Distance: " + distance);
			}
			sc.close();
		}catch(IOException e) {
			UI.println("Error: " + e);
		}
		return stations;
	}
	
	public List<String> routeReader() {
		List<String> stations = new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File(this.fileName));
			while(sc.hasNext()) {
				String station = sc.next();
				stations.add(station);
			}
			sc.close();
		}catch(IOException e) {
			UI.println("Error: " + e);
		}
		return stations;
	}
	
	public List<TrainService> serviceReader(TrainLine tl) {
		List<TrainService> services = new ArrayList <>();
		try {
			Scanner sc = new Scanner(new File(this.fileName));	
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				Scanner lineSc = new Scanner (line);
				TrainService ts = new TrainService(tl);//create an object of train service using train line as parametre
				
				List<Integer> times = new ArrayList<>();
				
				while(lineSc.hasNextInt()) {
					int time = lineSc.nextInt();
					times.add(time);					
				}
				ts.setTimes(times);
				services.add(ts);
				lineSc.close();
			}
			sc.close();
		}catch(IOException e) {
			UI.println("Error: " + e);
		}
		
		return services;
		
	}
	
	
}
