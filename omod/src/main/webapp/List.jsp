<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@ page import="org.openmrs.Concept" %>
<%@ page import="org.openmrs.api.context.Context" %>


<%
	try {

        List<Concept> list = new LinkedList<Concept>();
        list = Context.getConceptService().getAllConcepts();
        List<String> li = new ArrayList<String>();
        String a;
        for (Concept all : list) {
            if (all.getName().getName().equals("Radiology")) {
                List<Concept> conceptSets = new LinkedList();
                conceptSets = all.getSetMembers();
                for (Concept sets : conceptSets) {
                    li.add(sets.getName().getName());
                    a=sets.getName().getName();
                    %>
                        <div class="marginTable"  data-count="5">
                        <%=a%>
                        </div><%
                }
            }
        }




		String[] str = new String[li.size()];
		Iterator it = li.iterator();

		int i = 0;
		while (it.hasNext()) {
			String p = (String) it.next();
			str[i] = p;
			i++;
		}

		//jQuery related start
		String query = (String) request.getParameter("q");

		int cnt = 1;
		for (int j = 0; j < str.length; j++) {
			if (str[j].toUpperCase().startsWith(query.toUpperCase())) {
				out.print(str[j] + "\n");
				if (cnt >= 10)// 5=How many results have to show while we are typing(auto suggestions)
					break;
				cnt++;
			}
		}
		//jQuery related end

	} catch (Exception e) {
		e.printStackTrace();
	}

	//http://corejavaexample.blogspot.in/
%>