import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import api from '@/lib/api';
import { Star, Eye, MessageSquare } from 'lucide-react';

interface Book {
  id: number;
  title: string;
  description: string;
  authorName: string;
  genreName: string;
  averageRating: number;
  reviewsCount: number;
  viewsCount: number;
}

interface Review {
  id: number;
  rating: number;
  comment: string;
  createdAt: string;
}

export function BookDetailPage() {
  const { id } = useParams();
  const [book, setBook] = useState<Book | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [newReview, setNewReview] = useState({ rating: 5, comment: '' });

  useEffect(() => {
    loadBook();
    loadReviews();
  }, [id]);

  const loadBook = async () => {
    try {
      const response = await api.get(`/books/${id}`);
      setBook(response.data);
    } catch (error) {
      console.error('Ошибка загрузки книги:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadReviews = async () => {
    try {
      const response = await api.get(`/reviews/books/${id}`);
      setReviews(response.data.content || []);
    } catch (error) {
      console.error('Ошибка загрузки отзывов:', error);
    }
  };

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post(`/reviews/books/${id}`, newReview);
      setNewReview({ rating: 5, comment: '' });
      loadReviews();
      loadBook();
    } catch (error) {
      console.error('Ошибка отправки отзыва:', error);
    }
  };

  if (loading) {
    return <div className="text-center py-12">Загрузка...</div>;
  }

  if (!book) {
    return <div className="text-center py-12">Книга не найдена</div>;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <Card>
        <CardHeader>
          <CardTitle className="text-3xl">{book.title}</CardTitle>
          <CardDescription className="text-lg">{book.authorName}</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center gap-6 text-sm">
            <span className="flex items-center gap-2">
              <Star className="h-5 w-5 text-yellow-500" />
              <span className="font-semibold">{book.averageRating.toFixed(1)}</span>
              <span className="text-muted-foreground">({book.reviewsCount} отзывов)</span>
            </span>
            <span className="flex items-center gap-2">
              <Eye className="h-5 w-5" />
              {book.viewsCount} просмотров
            </span>
            {book.genreName && (
              <span className="px-3 py-1 bg-primary/10 text-primary rounded-full">
                {book.genreName}
              </span>
            )}
          </div>
          
          <div className="prose max-w-none">
            <p className="text-muted-foreground">{book.description}</p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5" />
            Отзывы ({reviews.length})
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <form onSubmit={handleSubmitReview} className="space-y-4 border-b pb-6">
            <h3 className="font-semibold">Оставить отзыв</h3>
            <div className="space-y-2">
              <label className="text-sm font-medium">Оценка</label>
              <select
                value={newReview.rating}
                onChange={(e) => setNewReview({ ...newReview, rating: Number(e.target.value) })}
                className="w-full px-3 py-2 border rounded-md"
              >
                {[5, 4, 3, 2, 1].map(rating => (
                  <option key={rating} value={rating}>{rating} звезд</option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium">Комментарий</label>
              <textarea
                value={newReview.comment}
                onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                className="w-full px-3 py-2 border rounded-md"
                rows={4}
                required
              />
            </div>
            <Button type="submit">Отправить отзыв</Button>
          </form>

          <div className="space-y-4">
            {reviews.map((review) => (
              <div key={review.id} className="border-b pb-4">
                <div className="flex items-center gap-2 mb-2">
                  <div className="flex">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <Star
                        key={i}
                        className={`h-4 w-4 ${
                          i < review.rating ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-sm text-muted-foreground">
                    {new Date(review.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-sm">{review.comment}</p>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
