package assignment_jocelyn;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ecs100.UI;

public class Main {

	private List<TrainLine> trainLines = new ArrayList<>();
	private Map<String, TrainLine> trainLineMap = new HashMap<>();
	
	private Map<Integer, Double> zoneFareMap = new HashMap<>();
	
	private List<Station> stations = new ArrayList<>();
	private Map<String, Station> stationMap = new HashMap<>();
	
	private Map<String, TrainService> serviceMapByID = new HashMap<>();
	
	private String pic1 = "Train network data/system-map.png";
	
	
	public void readFiles() {
		//train line
		DataReader tlReader = new DataReader("Train network data/train-lines.data");
		trainLines = tlReader.trainLinesReader();
		//a Map, indexed by the names of the TrainLines
		for(TrainLine trainLine : trainLines) {
			trainLineMap.put(trainLine.getName().toLowerCase(), trainLine);
		}
		
		//fare zone
		DataReader fzReader = new DataReader("Train network data/fares.data");
		zoneFareMap = fzReader.fareReader();
		
		//stations
		DataReader stReader = new DataReader("Train network data/stations.data");
		stations = stReader.stationReader();
		//a Map, indexed by the names of the stations
		for(Station st : stations) {
			stationMap.put(st.getName().toLowerCase(), st);
		}
		
		//routes&services
		for(TrainLine trainLine : trainLines) {			
			String routeFile = "Train network data/" + trainLine.getName() + "-stations.data";
			String serviceFile = "Train network data/" + trainLine.getName() + "-services.data";
			
			DataReader rtReader = new DataReader(routeFile);
			DataReader svReader = new DataReader(serviceFile);
			
			List<String> route = rtReader.routeReader();
			for (String rT : route) {
				String rt = rT.toLowerCase();
				Station st = stationMap.get(rt);
				trainLine.addStation(st);
				st.addTrainLine(trainLine);
			}
						
			List<TrainService> trainServices = svReader.serviceReader(trainLine);
			trainLine.setTrainService(trainServices);
			for(TrainService ts: trainServices) {
				ts.setTrainID();
				this.serviceMapByID.put(ts.getTrainID(), ts);
			}
					

		}
		
	}

	public void listStations() {
		TreeSet<String> sts = new TreeSet<>();
		for (Station st: this.stations) {
			sts.add(st.getName());			
		}
		for(String name : sts) {
			UI.println(name);
		}
	}
		
	public void listTrainLines() {
		TreeSet<String> tls = new TreeSet<>();
		for(TrainLine tl: this.trainLines) {
			tls.add(tl.getName());
		}
		for(String name : tls) {
			UI.println(name);
		}
	}
	
	public void listLineByStation() {
		String stationName = UI.askString("Enter station: ").toLowerCase();
		Station station = this.stationMap.get(stationName);
		if (station == null) {
			UI.println("Station invalid. You must enter the right station.");
			return; 
		}		
		Set<TrainLine> lines = station.getTrainLines();
		for(TrainLine tl: lines) {
			UI.println(tl.getName());
		}		
	}
	
	public void listStationByLine() {
		String lineName = UI.askString("Enter line: ");
		for(TrainLine tl: this.trainLines) {
			if(tl.getName().equalsIgnoreCase(lineName)) {
				List <Station> stations = tl.getStations();
				for(Station st : stations) {
					UI.println(st);
				}
				return;
			}
		}
		UI.println("Line not found. Please enter a valid line name.");
	}
	
 	public void findServiceByID() {
		String id = UI.askString("Enter train id");
		TrainService ts = this.serviceMapByID.get(id);
		UI.println(ts.getTimes());
	}
	
	public void findRoute() {
		String startStation = UI.askString("Enter start station: ");
		String endStation = UI.askString("Enter end station: ");
		
		List<TrainLine> routeLine = this.isOnSameLine(startStation, endStation);
		
		if (!routeLine.isEmpty()) {
			this.sameLineRoute(startStation, endStation, routeLine);
		}else {
			UI.println("Not on same line");
			this.transferByWellington(startStation, endStation);
		}
	}
	
	public List<TrainLine> isOnSameLine(String startStation, String endStation) {
		List<TrainLine> TLs = new ArrayList<>();
		for(TrainLine tl : trainLines) {
			List<Station> stations = tl.getStations();
			boolean findStart = false;
			boolean findEnd = false;
			
			for(Station st: stations) {
				if (st.getName().equalsIgnoreCase(startStation)) 
					findStart = true;
				if (st.getName().equalsIgnoreCase(endStation))
					findEnd = true;								
			}
			if (findStart && findEnd ) {
				TLs.add(tl);
			}
		}		
		return TLs;
	}
	
	public void sameLineRoute(String startStation, String endStation,List<TrainLine> routeLine) {		
		UI.println();
		UI.println("The route from " + startStation + " to " + endStation + ": ");
		for(TrainLine tl : routeLine) {			
			List<Station> stations = tl.getStations();
			int startIndex = -1;
			int endIndex = -1;	
			for(int i = 0; i < stations.size(); i++) {
				if(stations.get(i).getName().equalsIgnoreCase(startStation)) 
					startIndex = i;
				if(stations.get(i).getName().equalsIgnoreCase(endStation))
					endIndex = i;
			}
			if(startIndex < endIndex) {
				UI.println();
				UI.println("Train line: " + tl.getName());
				for(int i = startIndex; i <= endIndex; i++) {
					UI.println(stations.get(i).getName());
				}					
			}
		}		
	}
	
	//we assume that wellington is the transfer station
	public void transferByWellington(String startStation, String endStation) {
		UI.println("The route from " + startStation + " to " + endStation + ", transfer at Wellington station: ");
		for(TrainLine tl : trainLines) {
			List<Station> stations = tl.getStations();
			int startIndex = -1;
			int wellyIndex = -1;
			for(int i = 0; i < stations.size(); i++) {
				if(stations.get(i).getName().equalsIgnoreCase(startStation)) 
					startIndex = i;
				if(stations.get(i).getName().equalsIgnoreCase("Wellington")) 
					wellyIndex = i;				
			}
			
			if(startIndex != -1 && wellyIndex != -1 && startIndex < wellyIndex) {
				UI.println();
				UI.println("Train line: " + tl.getName());
		        UI.println("From " + startStation + " to Wellington:");
				for(int i = startIndex; i <= wellyIndex; i++) {
					UI.println(stations.get(i).getName());
				}	
			}			
		}
		
		for(TrainLine tl : trainLines) {
			List<Station> stations = tl.getStations();
			int wellyIndex = -1;
			int endIndex = -1;
			for(int i = 0; i < stations.size(); i++) {
				if(stations.get(i).getName().equalsIgnoreCase("Wellington")) 
					wellyIndex = i;	
				if(stations.get(i).getName().equalsIgnoreCase(endStation)) 
					endIndex = i;	
			}
			if(wellyIndex != -1 && endIndex != -1 && wellyIndex < endIndex) {
				UI.println();
				UI.println("Transfer train line: " + tl.getName());
				for(int i = wellyIndex; i <= endIndex; i++) {
					UI.println(stations.get(i).getName());
				}	
			}
		}		
	}
		
	public void findNextService() {
		String stationName = UI.askString("Enter station:").toLowerCase();
		int time = UI.askInt("Enter time in 24-hour format (e.g., 1430 for 2:30 PM): ");
		String[] idAndTime = this.nextService(stationName, time);
		UI.println("Next train service at station " + stationName + " is line " + idAndTime[0] + " at: " + idAndTime[1]);
		
	}
	
	public String [] nextService(String stationName, int time) {		
		Station station = this.stationMap.get(stationName);
	
		if (station == null) {
			UI.println("Station not found.");
            return null;
		}
		
		Integer nextTime = null;
		String nextTrainID = null;
		
		for(TrainLine tl : station.getTrainLines()) {
			List<TrainService> trainServices = tl.getTrainServices();
		
			for(TrainService ts: trainServices) {
				 List<Integer> times = ts.getTimes();
				 int stationIdx = tl.getStations().indexOf(station);				 
				 
				 if (stationIdx >= 0 && stationIdx < times.size()) {
					 int stationTime = times.get(stationIdx);
					 
					 if (stationTime != -1 && stationTime > time) {
						 if (nextTime == null || nextTime > stationTime) {
							 nextTime = stationTime;
							 nextTrainID = ts.getTrainID();
						 }
					 }					 
				 }				 
			}									
		}
		
		if(nextTime != null) {
			String [] idAndTime = new String[2];
			idAndTime[0] = nextTrainID;
			idAndTime[1] = String.valueOf(nextTime);
			return idAndTime;
			
		}else {
			UI.println("Next service not found.");
			return null;
			
		}		
	}
	
	public void findTripBetweenStations() {
		UI.println("Please enter two stations on the same train line.");
		String startStation = UI.askString("Enter start station: ").toLowerCase();
		String endStation = UI.askString("Enter destination station: ").toLowerCase();
		Station start = this.stationMap.get(startStation);
		Station end = this.stationMap.get(endStation);
		if (start == null || end == null) {
	        UI.println("One or both of the stations are invalid.");
	        return;
	    }
		
		List<TrainLine> trainLines = this.isOnSameLine(startStation, endStation);	
		if(trainLines.isEmpty()) {
			UI.println("Not on same line.");
			return;
		}
		
		int time = UI.askInt("Enter time in 24-hour format (e.g., 1430 for 2:30 PM): ");
		UI.println();
		
		String[] idAndTime = this.nextService(startStation, time);		
		while (idAndTime != null) {
			String id = idAndTime[0];
			int startTime = Integer.parseInt(idAndTime[1]);//transfer to int			
			
			TrainService ts = this.serviceMapByID.get(id);
			TrainLine tl = ts.getTrainLine();
			
			int startIdx = ts.getTimes().indexOf(startTime);
			int endIdx = tl.getStations().indexOf(end);
				
			if(startIdx < endIdx) {
				int endTime = ts.getTimes().get(endIdx);
				UI.println("Next train service start at station " + start.getName() + " is line " + id + " at: " + startTime);
				UI.println("Next train service arrive at station " + end.getName() + " at: " + endTime);
				
				int zone = this.calculateZone(start, end);
				UI.println("The number of zones this trip goes through: " + zone);
				double fare = this.zoneFareMap.get(zone);
				UI.println("Fare for the trip is: $" + fare);
				return;
			}
			idAndTime = this.nextService(start.getName(), startTime+1);
		}
		
		UI.println("Train service not found.");
	}
	
	public int calculateZone(Station start, Station end) {		
		int startZone = start.getZone();
		int endZone = end.getZone();
		int zone = Math.abs(startZone - endZone) + 1;
		return zone;
	}
	
//	public void bestTrip() {
//		String startStation = UI.askString("Enter start station: ");
//		String endStation = UI.askString("Enter end station: ");
//		int time = UI.askInt("Enter time: ");
//		this.findBestTrip(startStation, endStation, time);
//	}
//	
//	public void findBestTrip(String startStationName, String endStationName, int startTime) {
//		Station startStation = stationMap.get(startStationName.toLowerCase());
//		Station endStation = stationMap.get(endStationName.toLowerCase());
//
//		if (startStation == null || endStation == null) {
//			UI.println("Invalid station name(s).");
//			return;
//		}
//	    
//		List<TrainLine> sameLineLines = isOnSameLine(startStationName, endStationName);
//		
//		if (!sameLineLines.isEmpty()) {			
//			sameLineRouteWithTime(startStation, endStation, startTime);
//	    }
//  
//	}
	public void drawPic() {	
		UI.drawImage(pic1, 0, 0);	
	}
		
	public void displayStation() {
		UI.addSlider("Select Zone", 1, 14, 1, this::displayStationByZone);
	}
	
	public void displayStationByZone(double zone) {
		int zoneNum = (int) zone;
		UI.println("Station(s) within zone" + zoneNum + " : ");
		for(Station st : stations) {
			if(zoneNum == st.getZone()) {
				UI.println(st.getName());			
			}
		}		
	}
		
	public void printStationByClickingLine() {
		this.drawPic();
		UI.setMouseListener(this::doMouse);
	}
	
	public void doMouse(String action, double x, double y) {
		if(action.equals("clicked")) {
			String tlName = this.checkTrainLineByMouse(x, y).toLowerCase();
			UI.println(tlName);
			TrainLine tl = this.trainLineMap.get(tlName);
			List<Station> stations = tl.getStations();
			for(Station st : stations) {
				UI.println(st.getName());
			}
		}
	}
	
	public String checkTrainLineByMouse(double x, double y) {
		 Object[][] trainLines = {
				 {17, 158, 414, 434, "Johnsonville_Wellington"},
				 {123, 267, 282, 302, "Waikanae_Wellington"},
				 {255, 373, 426, 448, "Melling_Wellington"},
				 {238, 381, 248, 269, "Upper-Hutt_Wellington"},
				 {307, 485, 15, 35, "Masterton_Wellington"}
		 };
		 for (Object[] line : trainLines) {
			 int left = (int) line[0];
			 int right = (int) line[1];
			 int top = (int) line[2];
			 int bottom = (int) line[3];
			 String trainLineName = (String) line[4];

			 if (x >= left && x <= right && y >= top && y <= bottom) {		            
				 return trainLineName;
			 }
		 }
		 UI.println("Please click on a valid train line.");
		    return null; 		
	}
	
	public Main() {
		UI.initialise();
		this.readFiles();
		UI.addButton("Show map ", this::drawPic);
		UI.addButton("Clear text", UI::clearText);
		UI.addButton("List all stations", this::listStations);
		UI.addButton("List all train lines", this::listTrainLines);
		UI.addButton("List lines by station", this::listLineByStation);
		UI.addButton("List stations by lines", this::listStationByLine);
		UI.addButton("List stations by clicking lines", this::printStationByClickingLine);		
		UI.addButton("Find train service by id", this::findServiceByID);
		UI.addButton("Find route", this::findRoute);
		UI.addButton("Find next service", this::findNextService);
		UI.addButton("Find trip between stations", this::findTripBetweenStations);
		UI.addButton("Display station by zone", this::displayStation);
		
	}
	
	public static void main(String[] args) {
		new Main();
	}

}
