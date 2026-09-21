package com.visualnotes.ai;

import com.visualnotes.dto.PageContentDto;
import com.visualnotes.dto.PagePlanDto;
import com.visualnotes.dto.PromptAnalysisResponse;

public interface AIProvider {
    String getProviderName();
    
    PromptAnalysisResponse analyzePrompt(String prompt, String preferredStyle, Integer requestedPageCount);
    
    PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty);
    
    PageContentDto generatePageContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType
    );
    
    PageContentDto regeneratePage(
            PageContentDto currentContent,
            String instruction,
            String customModifier,
            String style
    );
}
