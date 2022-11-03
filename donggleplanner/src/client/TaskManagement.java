package client;

import java.awt.DisplayMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import server.DataAccessObject;
import server.ServerController;

public class TaskManagement {
       LocalDate today = LocalDate.now();
       
    private ServerController server;   
       
	public TaskManagement() {
		server = new ServerController();
	}

	public Object taskController(int selection, String accessCode, int month) {
		Object result = null;
		switch(selection) {
		case 11:
			result = this.makeTaskCalendarCtl(accessCode, month);
		
			break;
		}
		return result;
	}

	/* 같은 이름의 taskController라는 메소드를 하나 더 추가로 만든다
	 * 그 이유는 같은 역할이지만 파라미터 값이 다르거나, 타입이 다른 경우 이렇게 만든다
	 *  */
	public Object taskController(String clientData) {
	
		Object result = null;
		/* 호출로 불러온 clientData의 serviceCode를 추출하여 사용하기 위해서 */
		switch (clientData.split("&")[0].split("=")[1]) {
		case "12":
			result = this.getTaskListCtl(clientData);
			break;
		}
		
		return result;
	}
	
	
	
	
	
	/* 특정 달의 Task Calendar 생성하기 */
	private Object makeTaskCalendarCtl(String accessCode, int month) {
		ServerController server = new ServerController();
		
		/* P, N을 선택시 달력의 이동을 위한 
		 
		 * 필드를 선언한 LocalDate를 현재의 구문에서 저장하여 현재의 달이 아닌
		 * P나 N을 입력하면 그 값이 저장되어 지속되게 하는 구문
		 *  */
	      today = today.plusMonths(month);
		/* taskDays에 할일을 배열로 넣는 작업 */
		int[] taskDays = this.getTaskDays(server.controller("serviceCode=9&accessCode="
		+accessCode+"&date="
				+today.format(DateTimeFormatter.ofPattern("yyyyMM"))));

		return this.makeCalendar(taskDays, today);
		
		
	}
	
	/* 등록된 모든 할일 리스트 가져오기 */
	private Object getTaskListCtl(String clientData) {
		
		return this.makeTodoList(this.server.controller(clientData).split(":"));
		
		
	}
	
	
	
	/* 등록된 할 일 리스트의 정렬만들기 */
	private String makeTodoList(String[] record) {
		
		StringBuffer buffer = new StringBuffer();
		String temp;
		
		/* 정렬만들기 */
		for(int idx=0; idx<record.length; idx++) {
			
			if(idx != record.length-1) {
				for(int subIdx=idx+1; subIdx<record.length; subIdx++) {
					if(Integer.parseInt(record[idx]) > Integer.parseInt(record[subIdx])){
						temp = record[idx];
						record[idx] = record[subIdx];
						record[subIdx] = temp;
						
					}
				}
			}
		}
		String[][] toDoList = new String[record.length][];
		
		for(int idx=0; idx<record.length; idx++) {
			toDoList[idx] = record[idx].split(",");
		}
		
		String now;
		int itemCount = 0, beginIdx = 0;
		
		while(true) {
			now = toDoList[beginIdx][0];
			itemCount = this.itemCount(toDoList, now);
			
			buffer.append("____________________________________________________\n\n");
			buffer.append("To do List("+now.substring(0,8)+". "+now.substring(9,11)+". "+now.substring(11,13)+"/"+itemCount);
			buffer.append("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓\n");
			buffer.append("\n   Status              Content              Enable\n");
			buffer.append("\n__________________________________________________\n\n");
	
			for(int itemIdx=beginIdx; itemIdx<(itemCount+beginIdx); itemIdx++) {
				
				if(toDoList[itemIdx][4].equals("0")) {
					buffer.append("     □");
				}else if(toDoList[itemIdx][4].equals("1")) {
					buffer.append("      !");
				}else if(toDoList[itemIdx][4].equals("2")) {
					buffer.append("     ■");
				}
//				if(toDoList[itemIdx][4] != null)
//					buffer.append(toDoList[itemIdx][4].equals("1")? ("!"): 
//						toDoList[itemIdx][4].equals("-1")? ("?"): 
//							toDoList[itemIdx][4].equals("0")? ("□"):("■"));
				buffer.append(toDoList[itemIdx][1]+"\n");
				buffer.append("------------------------------------------------\n");
				buffer.append("________________________________________________\n");
				buffer.append("________________________________________________\n");

			}
			if((beginIdx+itemCount) == toDoList.length) break;
			beginIdx += itemCount;
		}
		return buffer.toString();	
		}
	
	
	
//	private String makeList(String[] record, String[][] toDoList, String clientData ) {
//		StringBuffer buffer = new StringBuffer();
//		String[] TODO = null;
//		new DataAccessObject();
//		
//		
//		
//		while(true) {
//		
//			
//				
//			buffer.append(" To do List  [ "+clientData.split("&")[2].split("=")[1]+" /"+itemCount+"] \n");
//			buffer.append("\n\n");
//			buffer.append("〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓 〓\n");
//			if(toDoList[itemCount][4] != null)
//				buffer.append(toDoList[itemCount][4].equals("1")? ("!"): 
//					toDoList[itemCount][4].equals("-1")? ("?"): 
//						toDoList[itemCount][4].equals("0")? ("□"):("■"));
//			buffer.append(toDoList[1]+"\n");
//			buffer.append("------------------------------------------------\n");
//			buffer.append("________________________________________________\n");
//			buffer.append("________________________________________________\n");
//			for(int itemIdx=beginIdx; itemIdx<(beginIdx+itemCount); itemIdx++) {
//
//			}
//			if((beginIdx+itemCount) == toDoList.length) break;
//			beginIdx += itemCount;
//		}
//		return TODO.toString();
//	}
	
	
		
		
		
		
	
	/* 특정일의 할일 건수 구하기 */
	private int itemCount(String[][] list, String compareValue) {
		int itemCount = 0;
		for(int idx=0; idx<list.length; idx++) {
			if(compareValue.equals(list[idx][0])) {
				itemCount +=1;
				break;
			}
		}
		return itemCount;
	}
	
	
	/* 할일 등록 하기 */
	private Object TaskRegistrationCtl(String accessCode) {
		
		
		
		return null;
	}
	/* 등록된 할일 수정하기 */
	private Object setModifyTaskCtl(String accessCode) {
		return null;
	}
	/* 할일에 대한 통계 만들기 */
	private Object getTaskStatCtl(String accessCode) {
		return null;
	}
	
	/*서버로부터 특정 달의 할 일이 등록되어있는 날짜가져오기 */
	private int[] getTaskDays(String serverData) {
		int [] taskDays = null;
		/* serverData의 할 일이 null값이면, 데이터를 호출했을 때
		 * " " 값이 이미 들어가있다. 그래서 데이터의 값이 " " 아니면 
		 * if구문이 실행되도록 만듬
		 *   */
		if(!serverData.equals("")) {
			String[] splitData = serverData.split(":");
			taskDays = new int[splitData.length];
		
			/*  */
		for(int idx=0; idx<taskDays.length; idx++) {
			taskDays[idx] = Integer.parseInt(splitData[idx]);
			
					
		}
		}
		
		return taskDays;
	}
	
	
	/* 특정 달의 할일이 등록되어있는 날짜를 특정 달의 달력에 표시하기 */
	private String makeCalendar(int[] taskDays, LocalDate today) {
		StringBuffer calendar = new StringBuffer();
		/* 한 주의 값을 가지고 오지만 Mon The ... 으로 오기때문에 
		 * 뒤에 getValue값을 넣어서 숫자값으로 가지고오도록 만든다
		 * */
		int dayOfWeek = LocalDate.of(today.getYear(), today.
				getMonthValue(),1).getDayOfWeek().getValue();
		int lastDay = today.lengthOfMonth();
		boolean isTask = false;
		
		/* 시작값인 1이 월요일로 시작되므로, 현재 사용예정인 달력에 적용하기위해서는
		 * 일요일을 1의 값으로 만드는게 개발의 입장에서 용이하기때문에, 
		 * 일요일의 7의 값을 1로 만들어준 것
		 *  */
		dayOfWeek = (dayOfWeek==7)? 1:dayOfWeek+1;
		calendar.append("◤TASK LIST◥〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓\n");
		calendar.append("\n");
		calendar.append("\n\tPrev  [ " + today.format(DateTimeFormatter.ofPattern("yyyy. MM.")) + " ]   Next\t \n");
		calendar.append("\n");
		calendar.append("  Sun   Mon   Tue   Wed   Thu   Fri   Sat   \n");

		/* 달의 첫 날인 1을 기준으로 잡아  */
		for(int dI=1-(dayOfWeek-1); dI<=lastDay; dI++) {
			if(dI<1) {
				calendar.append("      ");
			}else {
				/* dayIdx가(날짜)가 10이하인 경우와 많은 경우의 앞 줄의 띄워쓰기 값을 다르게 넣기위해  */
				calendar.append(dI<10? "   " + dI : "  " + dI);
				/* 할 일이 있는 날에 특수문자를 넣기위해 비교구문 */
				if(taskDays !=null) {
					
				for(int taskDayIdx=0; taskDayIdx<taskDays.length; taskDayIdx++) {
					if(dI == taskDays[taskDayIdx]) {
						isTask = true;
						break;
					}
				}
				}
				calendar.append(isTask?"+ ": "  ");
				isTask = false;
			}
			
			/* 일주일의 마지막인 토요일에 줄 바꿈하기 위해서 */
			calendar.append((dI+(dayOfWeek-1))%7==0? "\n" : "");
			/* 마지막 날에는 줄을 바꿔주기 위해서 */
			calendar.append(dI==lastDay? "\n": "");
		}
		
		calendar.append("●〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓 : ");
		
		
		return calendar.toString();
	}
}
