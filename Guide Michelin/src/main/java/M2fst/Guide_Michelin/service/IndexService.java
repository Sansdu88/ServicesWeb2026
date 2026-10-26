package M2fst.Guide_Michelin.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {
    private final StandardAnalyzer analyzer = new StandardAnalyzer();
    private final IndexWriter indexWriter;
    private DirectoryReader directoryReader;
    private IndexSearcher indexSearcher;

    public IndexService() throws IOException {
        Directory index = FSDirectory.open(Paths.get("index"));
        this.indexWriter = new IndexWriter(index, new IndexWriterConfig(analyzer));
        if (!DirectoryReader.indexExists(index)) {
            this.indexWriter.commit();
        }
        this.directoryReader = DirectoryReader.open(index);
        this.indexSearcher = new IndexSearcher(directoryReader);
    }

    public synchronized void indexEvaluation(Integer restaurantId, Integer evaluationId, String content) {
        try {
            Document document = new Document();
            document.add(new StringField("evaluationId", String.valueOf(evaluationId), Field.Store.YES));
            document.add(new StringField("restaurantId", String.valueOf(restaurantId), Field.Store.YES));
            document.add(new TextField("content", content, Field.Store.YES));
            indexWriter.addDocument(document);
            indexWriter.commit();
            refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void deleteEvaluation(Integer evaluationId) {
        try {
            indexWriter.deleteDocuments(new Term("evaluationId", String.valueOf(evaluationId)));
            indexWriter.commit();
            refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> searchEvaluations(String text) {
        try {
            Query query = new QueryParser("content", analyzer).parse(QueryParser.escape(text));
            TopDocs topDocs = indexSearcher.search(query, 100);
            List<Integer> ids = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = indexSearcher.storedFields().document(scoreDoc.doc);
                ids.add(Integer.valueOf(doc.get("evaluationId")));
            }
            return ids;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void refresh() throws IOException {
        DirectoryReader newReader = DirectoryReader.openIfChanged(directoryReader);
        if (newReader != null) {
            directoryReader.close();
            directoryReader = newReader;
            indexSearcher = new IndexSearcher(directoryReader);
        }
    }
}