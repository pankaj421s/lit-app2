package com.example.demo.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.*;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class LitService {
	public StringBuilder getStory(String url,String  title,StringBuilder sb) throws IOException
	{
		String text=null;
		//StringBuilder sb=new StringBuilder();
		try(Playwright playWright=Playwright.create())
		{
			Browser browser=playWright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			BrowserContext context=browser.newContext();
			Page page=context.newPage();
            page.setDefaultTimeout(60000);
			page.navigate(url);
			page.locator("._article_1kwe5_1").waitFor();
			String html=page.content();
			text=Jsoup.parse(html,url).select("._article_1kwe5_1").text();
			Element element=Jsoup.parse(html, url).selectFirst("._article_1kwe5_1");
			text=element.select("p, h1, h2, h3, li")
                    .eachText()
                    .stream()
                    .collect(Collectors.joining("\n\n"));
			sb.append(text);
			Element paginationContainer=Jsoup.parse(html, url).selectFirst(".panel._pagination_tl9pc_1");
            if(paginationContainer!=null) {
                Elements elements = paginationContainer.select("._pagination__item_tl9pc_16");
                //Elements elements=Jsoup.parse(html, url).select("._pagination__item_nqq7s_13");
              //  int i = 0;
				Element element2=elements.getLast();
				String[] maxPageurl=element2.attr("href").split("=");
				int maxPageno=Integer.parseInt(maxPageurl[1]);
				String[] pageFrag=element2.attr("href").split("=");

				for(int i=1;i<=maxPageno;i++)
				{
					if(i>1) {
						getPage(url+"?page="+i, sb);
					}

				}

                /*while (i < elements.size()) {
                    Element element2 = elements.get(i);
                    if (element2.hasAttr("href") && element2.classNames().size() == 1) {
                       getPage("https://www.literotica.com?page=" + element2.attr("href"), sb);
                    }

                    i++;

                }*/
            }
			
		}
		return sb;
	}
	public StringBuilder getPage(String url,StringBuilder sb) throws IOException
	{
		
		Document doc=Jsoup.connect(url).get();
		Element element=doc.selectFirst("._article_1kwe5_1");
		String text=element.select("p, h1, h2, h3, li")
                .eachText()
                .stream()
                .collect(Collectors.joining("\n\n"));
		sb.append("\n\n").append(text);
		return sb;
		
	}
public StringBuilder getSeries(String url,String title) throws IOException {
	try(Playwright playWright=Playwright.create()) {
		Browser browser=playWright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
		BrowserContext context=browser.newContext();
		Page page=context.newPage();
		page.navigate(url);
		page.locator("._wrapper_qr6sx_1").waitFor();
		String html=page.content();
        Element storyElement = Jsoup.parse(html,url).selectFirst("._title_q7ybb_26");
        title= storyElement.text();
		Element seriesWorksContainer=Jsoup.parse(html,url).selectFirst("._wrapper_qr6sx_1");
		Elements elements=seriesWorksContainer.select("._link_qr6sx_55");
		System.out.println(elements.size());
		StringBuilder sb=new StringBuilder();
      //  Element storyElement = elements.selectFirst("._item_title_1q129_227");
		for(Element element:elements)
		{
            sb.append("\n");
            sb=getStory(element.attr("href"),null,sb);
		//	sb=getPage("https://www.literotica.com"+element.attr("href"),sb);
			System.out.println(element);
		}
		return sb;



	}

}
    public void getSeriesStandalone(String url,String title) throws IOException {
        try(Playwright playWright=Playwright.create()) {
            Browser browser=playWright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context=browser.newContext();
            Page page=context.newPage();
            page.navigate(url);
            page.locator("._wrapper_qr6sx_1").waitFor();
            String html=page.content();
            Element storyElement = Jsoup.parse(html,url).selectFirst("._title_q7ybb_26");
            if(title.isBlank()||title==null) {
                title = storyElement.text();
            }
            Element seriesWorksContainer=Jsoup.parse(html,url).selectFirst("._wrapper_qr6sx_1");
            Elements elements=seriesWorksContainer.select("._link_qr6sx_55");
            System.out.println(elements.size());
            StringBuilder sb=new StringBuilder();

            //  Element storyElement = elements.selectFirst("._item_title_1q129_227");
            for(Element element:elements)
            {
                sb.append("\n");
                sb=getStory(element.attr("href"),null,sb);
                //	sb=getPage("https://www.literotica.com"+element.attr("href"),sb);
                System.out.println(element);
            }
            File f=new File(title);
            FileUtils.write(f, sb.toString(), Charset.forName("UTF-8"), true);



        }

    }
public StringBuilder getAuthors(String url,String title,boolean IsStories) throws IOException {

    File f=new File(title);
	try(Playwright playWright=Playwright.create()) {
		Browser browser = playWright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
		BrowserContext context = browser.newContext();
		Page page = context.newPage();
        page.setDefaultTimeout(60000);

        page.navigate(url);
		page.locator("._list_17z8h_5").waitFor();
		String html=page.content();
		Element authorWorkContainer=Jsoup.parse(html,url).selectFirst("._list_17z8h_5");
		Elements elements=authorWorkContainer.select("._card_17z8h_15");
		//elements.
		List<Element> elementList=elements.reversed();

		for(Element element:elementList)
		{
            StringBuilder sb=new StringBuilder();
			if(IsStories) {
				if (element.classNames().size() > 1&&element.classNames().contains("_part_card_last_17z8h_389")) {
					Element storyElement = element.selectFirst("._title_link_17z8h_66");
					System.out.println(storyElement.attr("href"));
					sb.append("\n").append(storyElement.text()).append("\n");
					sb.append(getStory(storyElement.attr("href"),null, sb));
				}
			}
				else {
					if(element.classNames().size()>1&&element.classNames().contains("._series_head_expanded_17z8h_39"))
					{
						Element storyElement=element.selectFirst("._title_link_17z8h_66");
						System.out.println(storyElement.attr("href"));
                     //   title=storyElement.text();
						sb.append("\n").append(storyElement.text()).append("\n");
						System.out.println(storyElement.text());
						//storyElement.text()
						sb.append(getSeries(storyElement.attr("href"),title));
						//sb.append(getPage(storyElement.attr("href"),sb ));

					} else if (element.classNames().size()>1&&element.classNames().contains("_part_card_last_17z8h_389")) {
						Element storyElement=element.selectFirst("._title_link_17z8h_66");
						System.out.println(storyElement.attr("href"));
						sb.append("\n").append(storyElement.text()).append("\n");
						sb=getStory(storyElement.attr("href"),null,sb );


					}
				}
            FileUtils.write(f, sb.toString(), Charset.forName("UTF-8"), true);
			}


		}



	return null;
}
	public StringBuilder getAuthors2(String url,String title,boolean IsStories) throws IOException {
		System.out.println("Hello");
		File f=new File(title);
		try(Playwright playWright=Playwright.create()) {
			Browser browser = playWright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			BrowserContext context = browser.newContext();
			Page page = context.newPage();


			page.navigate(url);
			page.setDefaultTimeout(60000);
			page.locator("._list_1epno_6").waitFor();

			Locator button=page.getByText("Show More");

			if(button!=null)
			{
				while(true)
				{
					//final int before= elements.size();
				//	count=before;
				//	final int count=

					if(!button.isVisible())
					{
						break;
					}
					button.click();
					try {
						page.waitForCondition(() -> !button.isDisabled(), new Page.WaitForConditionOptions().setTimeout(10000));

						page.waitForCondition(() ->
								"Show more".equals(button.textContent().trim())
						);
					}
					catch(PlaywrightException e)
					{
break;
					}
					////page.waitForCondition(()->elements.size()>before);
					//page.waitForCondition(() -> !button.isDisabled());
					//before= elements.size();
					//elements = authorWorkContainer.select(">*");
					//Thread.sleep(1000);
				//	currentCount=currentCount+1;
					//count++;


				}

			}



			//elements.
			String html = page.content();
			//Element authorWorkContainer = Jsoup.parse(html, url).selectFirst("._list_1jrij_6");
			Elements authorWorkContainer = Jsoup.parse(html, url).select("._wrapper_1epno_2");
			List<Element> authorWorkContainerList=authorWorkContainer.reversed();

		//	Elements elements=new Elements();
int count=0;
			for(Element element:authorWorkContainerList)
			{
				Element authorWorkSubContainer=element.selectFirst("._list_1epno_6");
				Elements elements = authorWorkSubContainer.select(">*");

				List<Element> reversedElements=elements.reversed();
				count+=elements.size();
				for(Element storyElement:reversedElements)
				{
					StringBuilder sb=new StringBuilder();
					if("article".equals(storyElement.tagName()))
					{
						getArticle(sb,storyElement);

					}
					else if("div".equals(storyElement.tagName()))
					{
						if(!IsStories) {
							getSeries(sb, storyElement);
						}

					}
					FileUtils.write(f, sb.toString(), Charset.forName("UTF-8"), true);
				}

			}

		//	Elements authorWorkContainer = Jsoup.parse(html, url).select("._list_1jrij_6");
			//Elements elements = authorWorkContainer.select("._card_17z8h_15");

			//final Elements elements = authorWorkContainer.select(">*");
		//	int currentCount= elements.size();
		//	List<Element> elementList = elements.reversed();


System.out.println(count);
		}

        return null;
	}
	public StringBuilder getArticle(StringBuilder sb,Element element) throws IOException {
		Element storyElement=element.select(">div>h3>a").first();
		sb.append("\n").append(storyElement.text()).append("\n");
		sb=getStory(storyElement.attr("href"),null, sb).append("\n");

		return sb;

	}
	public StringBuilder getSeries(StringBuilder sb,Element element) throws IOException
	{
		Element storyElement=element.select(">div>div>div>a").first();
		sb.append("\n").append(storyElement.text()).append("\n");
		sb.append(getSeries(storyElement.attr("href"),null)).append("\n");
		return sb;
	}

}
