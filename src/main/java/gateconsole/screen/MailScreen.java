package gateconsole.screen;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.messaging.MessageException;
import gate.messaging.Message;
import gate.messaging.Messenger;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Icon("2034")
@Name("Mensagens")
public class MailScreen extends Screen
{

    private List<Message> page
	    = new ArrayList<>();

    @Inject
    private Messenger messenger;

    public String call()
    {
	try
	{
	    page = messenger.search();
	} catch (MessageException ex)
	{
	    setMessages(ex.getMessages());
	}
	return "/WEB-INF/views/gateconsole/Mail/View.jsp";
    }

    public List<Message> getPage()
    {
	return page;
    }
}
