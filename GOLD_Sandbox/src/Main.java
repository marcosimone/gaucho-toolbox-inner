//Marco Simone
//Started: 2015-4-1
//Inner workings of a UCSB GOLD app to come
//This is just to write the alg to scrape and store the data
//after this is transforming this into an android app
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
public class Main {
	private static Map<String, String> cookies;
	private static List<Course>  courseArray;
	
	public static List<Course> getCourses(){return courseArray;};
	
	public static void main(String[] args) {
		courseArray= new ArrayList<Course>(); 
		try{
			cookies = getCookies();
		}catch(Exception e){
			System.out.println("Could not connect to GOLD");
			e.printStackTrace();
			System.exit(1);
		}
		Connection grabSchedule;
		
		grabSchedule= Jsoup.connect("https://my.sa.ucsb.edu/gold/StudentSchedule.aspx")
				.cookies(cookies)
				.timeout(5000)
				.userAgent("Mozilla/0.1 App Testing");
		
		
		
		Document schedulePage=null;
		try {
			schedulePage=grabSchedule.get();
			
			if(schedulePage.title().equals("Login")){
				System.out.println("Login failed");
				System.exit(1);
			}
			
		} catch (IOException e) {
			System.out.println("error: grabbing schedule");
			e.printStackTrace();
			System.exit(1);
		}
		

		Element classTable = schedulePage.getElementById("pageContent_CourseList");
		
		//courses with name and finalDate constructed
		Elements finals = schedulePage.getElementById("pageContent_FinalsGrid").select("td[class*=clcellprimary]");		
		
		for(int i=0;i<finals.size();i++){
			courseArray.add(new Course(finals.get(i).text().replace(String.valueOf((char) 160), " ").trim(), finals.get(i+1).text().replace(String.valueOf((char) 160), " ").trim()));
			i++;
		}
		
		
		
		//format and store data
		Elements exp = classTable.select("td[class*=clcellprimary]");
		List<String> rawData = new ArrayList<String>();
		for (int i = 0; i < exp.size(); i++) {
			
			String tmp;
			tmp=exp.get(i).text();
			tmp=tmp.replaceAll(Pattern.quote("course info /  /"), "");
			if(tmp.contains("http://"))
				tmp=tmp.substring(tmp.indexOf("Location: ")+9, tmp.indexOf("Campus Map"));
			tmp=tmp.replace(String.valueOf((char) 160), " ").trim();
			rawData.add(tmp);
		}
		
		String charPattern = "\\w( \\w)*";
		
		//UNKNOWN ERROR: WOULD APPRECIATE HELP
		int currentCourse=-1;
		for (int index=0;index<rawData.size();index++) {
			if(currentCourse<courseArray.size()-1 && rawData.get(index).equals(courseArray.get(currentCourse+1).getName())){
				currentCourse++;
				index++;
				courseArray.get(currentCourse).setEnrollCode(rawData.get(index));
				index++;
				courseArray.get(currentCourse).setGrading(rawData.get(index).charAt(0));
				index++;
				courseArray.get(currentCourse).setUnits(Double.parseDouble(rawData.get(index)));
			}else if(!rawData.get(index).matches(charPattern)){

				courseArray.get(currentCourse).addProfessor(rawData.get(index));
				
			}else{
				if(!courseArray.get(currentCourse).lectureSet()){
					
					courseArray.get(currentCourse).setLecture(rawData.get(index) + " " + rawData.get(index+1));
					index+=2;
					courseArray.get(currentCourse).setLectureRoom(rawData.get(index));
					
				}else{
					
					courseArray.get(currentCourse).setSection(rawData.get(index) + " " + rawData.get(index+1));
					index+=2;
					courseArray.get(currentCourse).setSectionRoom(rawData.get(index));
					
				}
				
				
				
			}
			
				
		}	
		for(Course c : courseArray){
			
			System.out.println(c);
		}
		
		
	}
	
	private static Map<String, String> getCookies(){
		
		String username="$USERNAME";
		String password="$PASSWORD";
		String VIEWSTATE;
		String VIEWSTATEGENERATOR;
		String EVENTVALIDATION;
		Map<String, String> cookies = new HashMap<String, String>();																																									
		
		//connect to login page to grab header data
		Document loginPage=null;
		Connection initialConnection=null;	
		try {
			initialConnection = Jsoup.connect("https://my.sa.ucsb.edu/gold/Login.aspx");
			loginPage=initialConnection.get();
			cookies.putAll(initialConnection.response().cookies());
			
		} catch (IOException e) {
			System.out.println("could not connect to Login page");
			e.printStackTrace();
			System.exit(1);
		}
		
		VIEWSTATE=loginPage.select("#__VIEWSTATE").val();
		VIEWSTATEGENERATOR=loginPage.select("#__VIEWSTATEGENERATOR").val();
		EVENTVALIDATION=loginPage.select("#__EVENTVALIDATION").val();
		
		
		//Use header data to log into gold then store cookies
		Response gatherCookies;
		try {
			gatherCookies = Jsoup.connect("https://my.sa.ucsb.edu/gold/Login.aspx")
					.cookies(cookies)
					.data("__LASTFOCUS", "", "__VIEWSTATE", VIEWSTATE, "__VIEWSTATEGENERATOR", VIEWSTATEGENERATOR, "__EVENTTARGET","", "_EVENTARGUMENT","", "__EVENTVALIDATION",EVENTVALIDATION,
						"ctl00$pageContent$userNameText", username, "ctl00$pageContent$passwordText", password, "ctl00$pageContent$loginButton.x", "100", "ctl00$pageContent$loginButton.y", "10", "ctl00$pageContent$PermPinLogin$userNameText","","ctl00$pageContent$PermPinLogin$passwordText","")
					.timeout(5000)
					.userAgent("Mozilla/0.1 App Testing")
					.method(Method.POST)
				    .execute();
			
			
			cookies.putAll(gatherCookies.cookies());
			
		} catch (IOException e) {
			System.out.println("error: gathering cookies");
			e.printStackTrace();
			System.exit(1);
		};
		
		return cookies;
		
	}

}

class Course{
	private String name;
	private String enrollCode;
	private char grading;
	private double units;
	private ArrayList<String> professors;
	private String lecture;
	private String lectureRoom;
	private String section;
	private String sectionRoom;
	private String finalDate;
	
	
	public Course(){professors=new ArrayList<String>();}
	public Course(String name){		
		this.name=name;
		professors=new ArrayList<String>();
	}
	
	public boolean lectureSet(){
		
		return lecture!=null;
	}
	
	public Course(String name, String finalDate){
		
		this.name=name;
		this.finalDate=finalDate;
	}
	//Big shout-out to eclipse getter/setter auto-fill
	public String getEnrollCode() {
		return enrollCode;
	}

	public void setEnrollCode(String enrollCode) {
		this.enrollCode = enrollCode;
	}

	public char getGrading() {
		return grading;
	}

	public void setGrading(char grading) {
		this.grading = grading;
	}

	public double getUnits() {
		return units;
	}

	public void setUnits(double units) {
		this.units = units;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addProfessor(String prof){
		
		professors.add(prof);
	}
	
	public ArrayList<String> getProfessors(){
		return professors;
	}
	
	public String getLecture() {
		return lecture;
	}

	public void setLecture(String lecture) {
		this.lecture = lecture;
	}

	public String getLectureRoom() {
		return lectureRoom;
	}

	public void setLectureRoom(String lectureRoom) {
		this.lectureRoom = lectureRoom;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getSectionRoom() {
		return sectionRoom;
	}

	public void setSectionRoom(String sectionRoom) {
		this.sectionRoom = sectionRoom;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}
	
	@Override
	public String toString() {
		String profs="";
		
		
		if(professors!=null){
			
			for(int i=0;i<professors.size()-1; i++){
				profs+=professors.get(i)+", ";
				
			}
			profs+=professors.get(professors.size()-1);
			
		}else{
			profs="none";
		}
		
		return "Course [name=" + name + ", enrollCode=" + enrollCode
				+ ", grading=" + grading + ", units=" + units + ", professors="
				+ profs + ", lecture=" + lecture + ", lectureRoom="
				+ lectureRoom + ", section=" + section + ", sectionRoom="
				+ sectionRoom + ", finalDate=" + finalDate + "]";
	}


	
	
	
}























